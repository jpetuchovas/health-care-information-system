import axios from 'axios';
import { state as globalState } from 'lape';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import VisitStatisticsComponent from './VisitStatisticsComponent';

const PAGE_SIZE = 8;

class VisitStatisticsContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = {
    startDate: '',
    endDate: '',
    visitAggregateCount: 0,
    visitDurationInMinutesAggregateSum: 0,
    visits: [],
    pageNumber: 1,
    hasNextPage: false,
    isLoading: false,
    isPageChanging: false,
    hasNoVisits: false,
  };

  handleApply = (event, picker) => {
    const startDate = picker.startDate.format('YYYY-MM-DD');
    const endDate = picker.endDate.format('YYYY-MM-DD');

    this.setState({
      startDate,
      endDate,
      isLoading: true,
    });

    axios
      .post(
        getRequestUrl(
          `/api/doctors/${globalState.userId}/statistics/visits/aggregate`
        ),
        {
          startDate,
          endDate,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        const visitAggregateCount = response.data.visitAggregateCount;
        const visitDurationInMinutesAggregateSum =
          response.data.visitDurationInMinutesAggregateSum;

        if (visitAggregateCount > 0) {
          axios
            .post(
              getRequestUrl(
                `/api/doctors/${globalState.userId}/statistics/visits/daily`
              ),
              {
                startDate,
                endDate,
                pageNumber: 1,
                pageSize: PAGE_SIZE,
              },
              getAuthorizationConfig()
            )
            .then(response => {
              this.setState({
                visitAggregateCount,
                visitDurationInMinutesAggregateSum,
                visits: response.data.elements,
                pageNumber: 1,
                hasNextPage: response.data.hasNext,
                isLoading: false,
                hasNoVisits: false,
              });
            })
            .catch(error => {
              if (error.response && error.response.status === 401) {
                logOut();
                this.context.router.push('/login');
              } else {
                this.setState({ isLoading: false });
              }
            });
        } else {
          this.setState({
            visitAggregateCount: 0,
            visitDurationInMinutesAggregateSum: 0,
            visits: [],
            pageNumber: 1,
            hasNextPage: false,
            isLoading: false,
            isPageChanging: false,
            hasNoVisits: true,
          });
        }
      })
      .catch(error => {
        if (error.response && error.response.status === 401) {
          logOut();
          this.context.router.push('/login');
        } else {
          this.setState({ isLoading: false });
        }
      });
  };

  handlePageSelect = eventKey => {
    this.setState({ isPageChanging: true });

    axios
      .post(
        getRequestUrl(
          `/api/doctors/${globalState.userId}/statistics/visits/daily`
        ),
        {
          startDate: this.state.startDate,
          endDate: this.state.endDate,
          pageNumber: eventKey,
          pageSize: PAGE_SIZE,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        this.setState({
          pageNumber: eventKey,
          visits: response.data.elements,
          hasNextPage: response.data.hasNext,
          isPageChanging: false,
        });
      })
      .catch(error => {
        if (error.response && error.response.status === 401) {
          logOut();
          this.context.router.push('/login');
        } else {
          this.setState({ isPageChanging: false });
        }
      });
  };

  render() {
    return (
      <VisitStatisticsComponent
        startDate={this.state.startDate}
        endDate={this.state.endDate}
        visitAggregateCount={this.state.visitAggregateCount}
        visitDurationInMinutesAggregateSum={
          this.state.visitDurationInMinutesAggregateSum
        }
        visits={this.state.visits}
        pageNumber={this.state.pageNumber}
        hasNextPage={this.state.hasNextPage}
        isLoading={this.state.isLoading}
        isPageChanging={this.state.isPageChanging}
        hasNoVisits={this.state.hasNoVisits}
        handleApply={this.handleApply}
        handlePageSelect={this.handlePageSelect}
      />
    );
  }
}

export default VisitStatisticsContainer;
