import { state as globalState } from 'lape';
import React, { Component } from 'react';

import AdministratorApp from '../administrator/AdministratorApp';
import { Role } from '../../common/constants';
import DoctorApp from '../doctor/DoctorApp';
import PasswordChangeFormContainer from './PasswordChangeFormContainer';
import PatientApp from '../patient/PatientApp';
import PharmacistApp from '../pharmacist/PharmacistApp';

class PasswordChangeContainer extends Component {
  render() {
    switch (globalState.role) {
      case Role.ADMIN: {
        return (
          <AdministratorApp>
            <PasswordChangeFormContainer />
          </AdministratorApp>
        );
      }
      case Role.DOCTOR: {
        return (
          <DoctorApp>
            <PasswordChangeFormContainer />
          </DoctorApp>
        );
      }
      case Role.PATIENT: {
        return (
          <PatientApp>
            <PasswordChangeFormContainer />
          </PatientApp>
        );
      }
      case Role.PHARMACIST: {
        return (
          <PharmacistApp>
            <PasswordChangeFormContainer />
          </PharmacistApp>
        );
      }
      default: {
        return null;
      }
    }
  }
}

export default PasswordChangeContainer;
