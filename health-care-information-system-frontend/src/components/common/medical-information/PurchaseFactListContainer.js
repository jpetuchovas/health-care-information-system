import axios from 'axios';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import PurchaseFactListComponent from './PurchaseFactListComponent';

const PAGE_SIZE = 35;

class PurchaseFactListContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  static propTypes = {
    patientId: PropTypes.string.isRequired,
    medicalPrescriptionId: PropTypes.string.isRequired,
  };

  state = {
    purchaseFacts: [],
    pageNumber: 1,
    hasNextPage: false,
    isLoading: false,
    isPageChanging: false,
  };

  getFirstPage = () => {
    this.setState({ isLoading: true });

    axios
      .post(
        getRequestUrl(
          `/api/patients/${this.props.patientId}/medical-prescriptions/${
            this.props.medicalPrescriptionId
          }/purchase-facts`
        ),
        {
          pageNumber: 1,
          pageSize: PAGE_SIZE,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        this.setState({
          purchaseFacts: response.data.elements,
          hasNextPage: response.data.hasNext,
          isLoading: false,
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
  };

  componentDidMount() {
    this.getFirstPage();
  }

  componentDidUpdate(previousProps) {
    if (
      previousProps.medicalPrescriptionId !== this.props.medicalPrescriptionId
    ) {
      this.getFirstPage();
    }
  }

  handlePageSelect = eventKey => {
    this.setState({ isPageChanging: true });

    axios
      .post(
        getRequestUrl(
          `/api/patients/${this.props.patientId}/medical-prescriptions/${
            this.props.medicalPrescriptionId
          }/purchase-facts`
        ),
        {
          pageNumber: eventKey,
          pageSize: PAGE_SIZE,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        this.setState({
          purchaseFacts: response.data.elements,
          pageNumber: eventKey,
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
      <PurchaseFactListComponent
        purchaseFacts={this.state.purchaseFacts}
        pageNumber={this.state.pageNumber}
        hasNextPage={this.state.hasNextPage}
        isLoading={this.state.isLoading}
        isPageChanging={this.state.isPageChanging}
        handlePageSelect={this.handlePageSelect}
      />
    );
  }
}

export default PurchaseFactListContainer;
