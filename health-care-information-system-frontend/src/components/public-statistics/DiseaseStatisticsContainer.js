import axios from 'axios';
import React, { Component } from 'react';

import { getRequestUrl } from '../../common/requestUtils';
import DiseaseStatisticsComponent from './DiseaseStatisticsComponent';

const DELAY_IN_MILLISECONDS = 50;

class DiseaseStatisticsContainer extends Component {
  state = {
    diseases: [],
    isLoading: false,
    existsNoData: false,
  };

  componentDidMount() {
    this.setState({ isLoading: true });

    axios
      .get(getRequestUrl('/api/public/statistics/diseases'))
      .then(response => {
        this.setState({
          isLoading: false,
          existsNoData: response.data.length === 0,
        });

        // setTimeout is used here to force animation on the bar chart.
        setTimeout(() => {
          this.setState({ diseases: response.data });
        }, DELAY_IN_MILLISECONDS);
      })
      .catch(() => {
        this.setState({ isLoading: false });
      });
  }

  render() {
    return (
      <DiseaseStatisticsComponent
        diseases={this.state.diseases}
        isLoading={this.state.isLoading}
        existsNoData={this.state.existsNoData}
      />
    );
  }
}

export default DiseaseStatisticsContainer;
