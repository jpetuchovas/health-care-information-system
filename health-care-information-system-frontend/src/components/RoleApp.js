import PropTypes from 'prop-types';
import React, { Fragment } from 'react';

import { MAIN_CONTENT_MARGIN } from '../common/constants';
import NavigationComponent from './common/navigation/NavigationComponent';

const styles = {
  mainContent: {
    margin: MAIN_CONTENT_MARGIN,
  },
};

const UserApp = props => (
  <Fragment>
    <NavigationComponent>{props.navigationComponent}</NavigationComponent>
    <div style={styles.mainContent}>{props.children}</div>
  </Fragment>
);

UserApp.propTypes = {
  navigationComponent: PropTypes.element.isRequired,
};

export default UserApp;
