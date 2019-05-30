import PropTypes from 'prop-types';
import axios from 'axios';
import React, { Component } from 'react';

import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import PatientAssignmentComponent from './PatientAssignmentComponent';

const PAGE_SIZE = 5;

const initialState = {
  doctorSearchQuery: {
    firstName: '',
    lastName: '',
    specialization: '',
    username: '',
  },

  submittedDoctorSearchQuery: {
    firstName: '',
    lastName: '',
    specialization: '',
    username: '',
  },

  patientSearchQuery: {
    firstName: '',
    lastName: '',
    personalIdentificationNumber: '',
    username: '',
  },

  submittedPatientSearchQuery: {
    firstName: '',
    lastName: '',
    personalIdentificationNumber: '',
    username: '',
  },

  doctors: [],
  patients: [],
  doctorPageNumber: 1,
  patientPageNumber: 1,
  doctorHasNextPage: false,
  patientHasNextPage: false,
  selectedDoctor: { id: '', firstName: '', lastName: '' },
  selectedPatient: { id: '', firstName: '', lastName: '' },
  hasNotSearchedForDoctors: true,
  hasNotSearchedForPatients: true,
  existsNoDoctors: false,
  existsNoPatients: false,
  isDoctorSearchQueryLoading: false,
  isPatientSearchQueryLoading: false,
  isDoctorPageChanging: false,
  isPatientPageChanging: false,
  isDoctorPanelFocused: true,
  isPatientPanelFocused: false,
  isSubmitting: false,
  isAssignmentSuccessful: true,
};

class PatientAssignmentContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = initialState;

  handleFieldChange = searchQueryName => {
    return fieldName => {
      return event => {
        this.setState({
          [searchQueryName]: {
            ...this.state[searchQueryName],
            [fieldName]: event.target.value,
          },
        });
      };
    };
  };

  getPrefixedNames = stateFieldName => {
    const prefix = stateFieldName.slice(0, -1);
    const prefixCapitalized = prefix.charAt(0).toUpperCase() + prefix.slice(1);
    return {
      searchQueryName: `${prefix}SearchQuery`,
      submittedSearchQueryName: `submitted${prefixCapitalized}SearchQuery`,
      pageNumberName: `${prefix}PageNumber`,
      hasNextPageName: `${prefix}HasNextPage`,
      isSearchQueryLoadingName: `is${prefixCapitalized}SearchQueryLoading`,
      isPageChangingName: `is${prefixCapitalized}PageChanging`,
      existsNoUsersName: `existsNo${prefixCapitalized}s`,
      hasNotSearchedForName: `hasNotSearchedFor${prefixCapitalized}s`,
    };
  };

  handleSearchButtonClick = stateFieldName => {
    return event => {
      event.preventDefault();

      const prefixedNames = this.getPrefixedNames(stateFieldName);
      const commonPayload = {
        firstName: this.state[prefixedNames.searchQueryName].firstName,
        lastName: this.state[prefixedNames.searchQueryName].lastName,
        username: this.state[prefixedNames.searchQueryName].username,
        pageNumber: 1,
        pageSize: PAGE_SIZE,
      };

      this.setState({ [prefixedNames.isSearchQueryLoadingName]: true });
      axios
        .post(
          getRequestUrl(`/api/${stateFieldName}/filter/admin`),
          stateFieldName === 'doctors'
            ? {
                ...commonPayload,
                specialization: this.state.doctorSearchQuery.specialization,
              }
            : {
                ...commonPayload,
                personalIdentificationNumber: this.state.patientSearchQuery
                  .personalIdentificationNumber,
              },
          getAuthorizationConfig()
        )
        .then(response => {
          const users = response.data.elements;
          const commonState = {
            [prefixedNames.submittedSearchQueryName]: this.state[
              prefixedNames.searchQueryName
            ],
            [stateFieldName]: users,
            [prefixedNames.pageNumberName]: 1,
            [prefixedNames.hasNextPageName]: response.data.hasNext,
            [prefixedNames.isSearchQueryLoadingName]: false,
            [prefixedNames.hasNotSearchedForName]: false,
          };

          if (this.state[prefixedNames.existsNoUsersName] && users.length > 0) {
            this.setState({
              ...commonState,
              [prefixedNames.existsNoUsersName]: false,
            });
          } else if (
            users.length === 0 &&
            Object.values(this.state[prefixedNames.searchQueryName]).every(
              value => !value
            )
          ) {
            this.setState({
              ...commonState,
              [prefixedNames.existsNoUsersName]: true,
            });
          } else {
            this.setState(commonState);
          }
        })
        .catch(error => {
          if (error.response && error.response.status === 401) {
            logOut();
            this.context.router.push('/login');
          } else {
            this.setState({ [prefixedNames.isSearchQueryLoadingName]: false });
          }
        });
    };
  };

  handlePageSelect = stateFieldName => {
    return eventKey => {
      const prefixedNames = this.getPrefixedNames(stateFieldName);
      const commonPayload = {
        firstName: this.state[prefixedNames.submittedSearchQueryName].firstName,
        lastName: this.state[prefixedNames.submittedSearchQueryName].lastName,
        username: this.state[prefixedNames.submittedSearchQueryName].username,
        pageNumber: eventKey,
        pageSize: PAGE_SIZE,
      };

      this.setState({ [prefixedNames.isPageChangingName]: true });
      axios
        .post(
          getRequestUrl(`/api/${stateFieldName}/filter/admin`),
          stateFieldName === 'doctors'
            ? {
                ...commonPayload,
                specialization: this.state.submittedDoctorSearchQuery
                  .specialization,
              }
            : {
                ...commonPayload,
                personalIdentificationNumber: this.state
                  .submittedPatientSearchQuery.personalIdentificationNumber,
              },
          getAuthorizationConfig()
        )
        .then(response => {
          this.setState({
            [prefixedNames.searchQueryName]: this.state[
              prefixedNames.submittedSearchQueryName
            ],
            [prefixedNames.pageNumberName]: eventKey,
            [stateFieldName]: response.data.elements,
            [prefixedNames.hasNextPageName]: response.data.hasNext,
            [prefixedNames.isPageChangingName]: false,
          });
        })
        .catch(error => {
          if (error.response && error.response.status === 401) {
            logOut();
            this.context.router.push('/login');
          } else {
            this.setState({ [prefixedNames.isPageChangingName]: false });
          }
        });
    };
  };

  handleTableRowClick = stateFieldName => {
    return event => {
      if (stateFieldName === 'selectedDoctor') {
        this.setState({
          isDoctorPanelFocused: false,
          isPatientPanelFocused: true,
        });
      }

      if (this.state.selectedDoctor.id || this.state.selectedPatient.id) {
        this.setState({
          isDoctorPanelFocused: true,
          isPatientPanelFocused: true,
        });
      }

      if (!this.state.isSubmitting) {
        this.setState({
          [stateFieldName]: {
            id: event.target.parentNode.id,
            firstName: event.target.parentNode.childNodes[0].textContent,
            lastName: event.target.parentNode.childNodes[1].textContent,
          },
        });
      }
    };
  };

  handleSubmit = event => {
    event.preventDefault();

    this.setState({ isSubmitting: true });

    axios
      .put(
        getRequestUrl(
          `/api/doctors/${this.state.selectedDoctor.id}/patients/${
            this.state.selectedPatient.id
          }`
        ),
        {},
        getAuthorizationConfig()
      )
      .then(() => {
        this.setState({
          ...initialState,
          isSubmitting: false,
          isAssignmentSuccessful: true,
        });
      })
      .catch(() => {
        this.setState({
          isSubmitting: false,
          isAssignmentSuccessful: false,
        });
      });
  };

  handleDoctorComponentMouseEnter = () => {
    this.setState({ isDoctorPanelFocused: true });
  };

  handleDoctorComponentMouseLeave = () => {
    if (this.state.selectedDoctor.id && !this.state.selectedPatient.id) {
      this.setState({ isDoctorPanelFocused: false });
    }
  };

  handlePatientComponentMouseEnter = () => {
    this.setState({ isPatientPanelFocused: true });
  };

  handlePatientComponentMouseLeave = () => {
    if (!this.state.selectedDoctor.id) {
      this.setState({ isPatientPanelFocused: false });
    }
  };

  render() {
    return (
      <PatientAssignmentComponent
        doctorSearchQuery={this.state.doctorSearchQuery}
        patientSearchQuery={this.state.patientSearchQuery}
        selectedDoctor={this.state.selectedDoctor}
        selectedPatient={this.state.selectedPatient}
        doctors={this.state.doctors}
        patients={this.state.patients}
        doctorPageNumber={this.state.doctorPageNumber}
        patientPageNumber={this.state.patientPageNumber}
        doctorHasNextPage={this.state.doctorHasNextPage}
        patientHasNextPage={this.state.patientHasNextPage}
        hasNotSearchedForDoctors={this.state.hasNotSearchedForDoctors}
        hasNotSearchedForPatients={this.state.hasNotSearchedForPatients}
        existsNoDoctors={this.state.existsNoDoctors}
        existsNoPatients={this.state.existsNoPatients}
        isDoctorSearchQueryLoading={this.state.isDoctorSearchQueryLoading}
        isPatientSearchQueryLoading={this.state.isPatientSearchQueryLoading}
        isDoctorPageChanging={this.state.isDoctorPageChanging}
        isPatientPageChanging={this.state.isPatientPageChanging}
        isDoctorPanelFocused={this.state.isDoctorPanelFocused}
        isPatientPanelFocused={this.state.isPatientPanelFocused}
        handleFieldChange={this.handleFieldChange}
        handleSearchButtonClick={this.handleSearchButtonClick}
        handlePageSelect={this.handlePageSelect}
        handleTableRowClick={this.handleTableRowClick}
        handleDoctorComponentMouseEnter={this.handleDoctorComponentMouseEnter}
        handleDoctorComponentMouseLeave={this.handleDoctorComponentMouseLeave}
        handlePatientComponentMouseEnter={this.handlePatientComponentMouseEnter}
        handlePatientComponentMouseLeave={this.handlePatientComponentMouseLeave}
        handleSubmit={this.handleSubmit}
        isSubmitting={this.state.isSubmitting}
        isAssignmentSuccessful={this.state.isAssignmentSuccessful}
      />
    );
  }
}
export default PatientAssignmentContainer;
