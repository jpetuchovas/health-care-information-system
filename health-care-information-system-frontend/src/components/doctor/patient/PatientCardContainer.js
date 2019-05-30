import axios from 'axios';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import PatientCardComponent from './PatientCardComponent';

const PAGE_SIZE = 10;

class PatientCardContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = {
    patient: {
      id:
        (this.props.location.state &&
          this.props.location.state.patient &&
          this.props.location.state.patient.id) ||
        '',
      firstName:
        (this.props.location.state &&
          this.props.location.state.patient &&
          this.props.location.state.patient.firstName) ||
        '',
      lastName:
        (this.props.location.state &&
          this.props.location.state.patient &&
          this.props.location.state.patient.lastName) ||
        '',
      personalIdentificationNumber:
        (this.props.location.state &&
          this.props.location.state.patient &&
          this.props.location.state.patient.personalIdentificationNumber) ||
        '',
      birthDate:
        (this.props.location.state &&
          this.props.location.state.patient &&
          this.props.location.state.patient.birthDate) ||
        '',
    },

    initialTab: this.props.location.pathname.endsWith('/medical-records')
      ? 1
      : 2,
  };

  componentDidMount() {
    const patientId = this.props.params.patientId;
    if (!Object.values(this.state.patient).some(value => !!value)) {
      axios
        .get(
          getRequestUrl(`/api/patients/${patientId}`),
          getAuthorizationConfig()
        )
        .then(response => {
          this.setState({
            patient: response.data,
          });
        })
        .catch(error => {
          if (error.response && error.response.status === 401) {
            logOut();
            this.context.router.push('/login');
          } else {
            this.context.router.replace('/not-found');
          }
        });
    }
  }

  handleTabSelect = key => {
    this.context.router.replace({
      pathname: `/patients/${this.state.patient.id}/${
        key === 1 ? 'medical-records' : 'medical-prescriptions'
      }`,
      state: { patient: this.state.patient },
    });
  };

  handleNewMedicalRecordClick = () => {
    this.context.router.push({
      pathname: '/medical-record',
      state: {
        patientId: this.state.patient.id,
        personalIdentificationNumber: this.state.patient
          .personalIdentificationNumber,
      },
    });
  };

  handleNewMedicalPrescriptionClick = () => {
    this.context.router.push({
      pathname: '/medical-prescription',
      state: {
        patientId: this.state.patient.id,
        personalIdentificationNumber: this.state.patient
          .personalIdentificationNumber,
      },
    });
  };

  render() {
    return (
      <PatientCardComponent
        patient={this.state.patient}
        pageSize={PAGE_SIZE}
        initialTab={this.state.initialTab}
        handleTabSelect={this.handleTabSelect}
        handleNewMedicalRecordClick={this.handleNewMedicalRecordClick}
        handleNewMedicalPrescriptionClick={
          this.handleNewMedicalPrescriptionClick
        }
      />
    );
  }
}

export default PatientCardContainer;
