import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { Tab, Tabs } from 'react-bootstrap';

import AdministratorRegistrationFormContainer from './administrator/AdministratorRegistrationFormContainer';
import DoctorRegistrationFormContainer from './doctor/DoctorRegistrationFormContainer';
import PatientRegistrationFormContainer from './patient/PatientRegistrationFormContainer';
import PharmacistRegistrationFormContainer from './pharmacist/PharmacistRegistrationFormContainer';
import './RegistrationTabs.css';

const getInitialTabFromPath = path => {
  switch (path) {
    case '/registration/patient': {
      return 1;
    }
    case '/registration/doctor': {
      return 2;
    }
    case '/registration/pharmacist': {
      return 3;
    }
    case '/registration/administrator': {
      return 4;
    }
    default: {
      return 1;
    }
  }
};

const getPathFromActiveKey = activeKey => {
  switch (activeKey) {
    case 1: {
      return '/registration/patient';
    }
    case 2: {
      return '/registration/doctor';
    }
    case 3: {
      return '/registration/pharmacist';
    }
    case 4: {
      return '/registration/administrator';
    }
    default: {
      return '/registration/patient';
    }
  }
};

class RegistrationTabs extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = {
    initialTab: getInitialTabFromPath(this.props.location.pathname),
  };

  handleSelect = key => {
    this.context.router.replace(getPathFromActiveKey(key));
  };

  render() {
    return (
      <Tabs
        defaultActiveKey={this.state.initialTab}
        id="registration"
        onSelect={this.handleSelect}
        justified
      >
        <Tab eventKey={1} title="Paciento registracija">
          <PatientRegistrationFormContainer />
        </Tab>

        <Tab eventKey={2} title="Gydytojo registracija">
          <DoctorRegistrationFormContainer />
        </Tab>

        <Tab eventKey={3} title="Vaistininko registracija">
          <PharmacistRegistrationFormContainer />
        </Tab>

        <Tab eventKey={4} title="Administratoriaus registracija">
          <AdministratorRegistrationFormContainer />
        </Tab>
      </Tabs>
    );
  }
}

export default RegistrationTabs;
