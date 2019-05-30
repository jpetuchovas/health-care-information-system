import axios from 'axios';
import React, { Component } from 'react';

import ActiveIngredientUsageStatisticsComponent from './ActiveIngredientUsageStatisticsComponent';
import { getRequestUrl } from '../../common/requestUtils';

const DELAY_IN_MILLISECONDS = 50;

class ActiveIngredientUsageStatisticsContainer extends Component {
  state = {
    activeIngredients: [],
    isLoading: false,
    existsNoData: false,
  };

  componentDidMount() {
    this.setState({ isLoading: true });

    axios
      .get(getRequestUrl('/api/public/statistics/active-ingredients'))
      .then(response => {
        this.setState({
          isLoading: false,
          existsNoData: response.data.length === 0,
        });

        setTimeout(() => {
          this.setState({ activeIngredients: response.data });
        }, DELAY_IN_MILLISECONDS);
      })
      .catch(() => {
        this.setState({ isLoading: false });
      });
  }

  render() {
    return (
      <ActiveIngredientUsageStatisticsComponent
        activeIngredients={this.state.activeIngredients}
        isLoading={this.state.isLoading}
        existsNoData={this.state.existsNoData}
      />
    );
  }
}

export default ActiveIngredientUsageStatisticsContainer;
