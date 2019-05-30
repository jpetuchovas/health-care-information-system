import axios from 'axios';
import { state as globalState } from 'lape';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import PatientListComponent from './PatientListComponent';

const PAGE_SIZE = 7;

class PatientListContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = {
    searchQuery: {
      firstName: '',
      lastName: '',
      personalIdentificationNumber: '',
      diseaseCode: '',
    },

    submittedSearchQuery: {
      firstName: '',
      lastName: '',
      personalIdentificationNumber: '',
      diseaseCode: '',
    },

    patients: [],
    pageNumber: 1,
    hasNextPage: false,
    isLoading: false,
    isPageChanging: false,
    hasNoPatients: true,
  };

  componentDidMount() {
    this.setState({ isLoading: true });

    axios
      .post(
        getRequestUrl(`/api/doctors/${globalState.userId}/patients`),
        {
          ...this.state.searchQuery,
          pageNumber: 1,
          pageSize: PAGE_SIZE,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        const patients = response.data.elements;
        const commonState = {
          submittedSearchQuery: this.state.searchQuery,
          patients: patients,
          hasNextPage: response.data.hasNext,
          isLoading: false,
        };

        if (patients.length === 0) {
          this.setState({
            ...commonState,
            hasNoPatients: true,
          });
        } else {
          this.setState({
            ...commonState,
            hasNoPatients: false,
          });
        }
      })
      .catch(error => {
        if (error.response && error.response.status === 401) {
          logOut();
          this.context.router.push('/login');
        } else {
          this.setState({ isLoading: false });
        }
      });
  }

  handleFieldChange = fieldName => {
    return event => {
      this.setState({
        searchQuery: {
          ...this.state.searchQuery,
          [fieldName]: event.target.value,
        },
      });
    };
  };

  handleSearchButtonClick = event => {
    event.preventDefault();

    this.setState({ isLoading: true });

    axios
      .post(
        getRequestUrl(`/api/doctors/${globalState.userId}/patients`),
        {
          ...this.state.searchQuery,
          pageNumber: 1,
          pageSize: PAGE_SIZE,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        const patients = response.data.elements;
        const commonState = {
          submittedSearchQuery: this.state.searchQuery,
          patients: patients,
          pageNumber: 1,
          hasNextPage: response.data.hasNext,
          isLoading: false,
        };

        if (this.state.hasNoPatients && patients.length > 0) {
          this.setState({
            ...commonState,
            hasNoPatients: false,
          });
        } else if (
          patients.length === 0 &&
          Object.values(this.state.searchQuery).every(value => !value)
        ) {
          this.setState({
            ...commonState,
            hasNoPatients: true,
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
          this.setState({ isLoading: false });
        }
      });
  };

  handlePageSelect = eventKey => {
    this.setState({ isPageChanging: true });

    axios
      .post(
        getRequestUrl(`/api/doctors/${globalState.userId}/patients`),
        {
          ...this.state.submittedSearchQuery,
          pageNumber: eventKey,
          pageSize: PAGE_SIZE,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        this.setState({
          searchQuery: this.state.submittedSearchQuery,
          pageNumber: eventKey,
          patients: response.data.elements,
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

  handleTableClick = event => {
    const parentNode = event.target.parentNode;
    const selectedPatientId = parentNode.id;
    this.context.router.push({
      pathname: `/patients/${selectedPatientId}`,
      state: {
        patient: {
          id: selectedPatientId,
          firstName: parentNode.childNodes[0].textContent,
          lastName: parentNode.childNodes[1].textContent,
          personalIdentificationNumber: parentNode.childNodes[2].textContent,
          birthDate: parentNode.childNodes[3].textContent,
        },
      },
    });
  };

  render() {
    return (
      <PatientListComponent
        searchQuery={this.state.searchQuery}
        submittedSearchQuery={this.state.submittedSearchQuery}
        patients={this.state.patients}
        pageNumber={this.state.pageNumber}
        hasNextPage={this.state.hasNextPage}
        isLoading={this.state.isLoading}
        isPageChanging={this.state.isPageChanging}
        hasNoPatients={this.state.hasNoPatients}
        handleFieldChange={this.handleFieldChange}
        handleSearchButtonClick={this.handleSearchButtonClick}
        handlePageSelect={this.handlePageSelect}
        handleTableClick={this.handleTableClick}
      />
    );
  }
}

export default PatientListContainer;
