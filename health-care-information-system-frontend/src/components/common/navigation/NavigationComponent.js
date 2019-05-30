import { state as globalState } from 'lape';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Image, Navbar } from 'react-bootstrap';

import AccountNavigationComponent from './AccountNavigationComponent';
import { getHomePage } from '../../../common/routingUtils';
import medkit from '../../../images/medkit.svg';

const styles = {
  brandLink: {
    cursor: 'pointer',
  },

  logo: {
    float: 'left',
    height: '23px',
    marginRight: '10px',
  },
};

class NavigationComponent extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  render() {
    return (
      <Navbar inverse staticTop fluid collapseOnSelect>
        <Navbar.Header>
          <Navbar.Brand
            style={styles.brandLink}
            onClick={() =>
              this.context.router.push(getHomePage(globalState.role))
            }
          >
            <span>Medika</span>
            <Image src={medkit} style={styles.logo} />
          </Navbar.Brand>
          <Navbar.Toggle />
        </Navbar.Header>

        <Navbar.Collapse>
          {React.Children.map(this.props.children, child =>
            React.cloneElement(child)
          )}
          <AccountNavigationComponent />
        </Navbar.Collapse>
      </Navbar>
    );
  }
}

export default NavigationComponent;
