import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Tab, Tabs } from 'react-bootstrap';

import { MAIN_CONTENT_MARGIN } from '../../common/constants';
import DiseaseStatisticsContainer from './DiseaseStatisticsContainer';
import ActiveIngredientUsageStatisticsContainer from './ActiveIngredientUsageStatisticsContainer';

const styles = {
  mainContent: {
    margin: MAIN_CONTENT_MARGIN,
  },

  tabs: {
    marginTop: '23px',
  },
};

class PublicStatisticsTabs extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = {
    initialTab:
      this.props.location.pathname === '/public-statistics/diseases' ? 1 : 2,
  };

  handleSelect = key => {
    this.context.router.replace(
      `/public-statistics/${key === 1 ? 'diseases' : 'active-ingredients'}`
    );
  };

  render() {
    return (
      <div style={styles.mainContent}>
        <Tabs
          defaultActiveKey={this.state.initialTab}
          id="public-statistics"
          onSelect={this.handleSelect}
          style={styles.tabs}
          justified
        >
          <Tab eventKey={1} title="Susirgimų statistika">
            <DiseaseStatisticsContainer />
          </Tab>

          <Tab eventKey={2} title="Vaistų vartojimo statistika">
            <ActiveIngredientUsageStatisticsContainer />
          </Tab>
        </Tabs>
      </div>
    );
  }
}

export default PublicStatisticsTabs;
