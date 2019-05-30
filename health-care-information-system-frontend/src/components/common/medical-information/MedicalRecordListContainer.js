import axios from 'axios';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import MedicalRecordListComponent from './MedicalRecordListComponent';

const selectMedicalRecordInitialState = {
  id: '',
  description: '',
  visitDurationInMinutes: '',
  diseaseCode: '',
  isVisitCompensated: null,
  isVisitRepeated: null,
  date: '',
  doctorFullName: '',
};

class MedicalRecordListContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  static propTypes = {
    patientId: PropTypes.string.isRequired,
    pageSize: PropTypes.number.isRequired,
    noMedicalRecordsNotificationTitle: PropTypes.string.isRequired,
    noMedicalRecordsNotificationQuestion: PropTypes.string.isRequired,
  };

  state = {
    medicalRecords: [],
    pageNumber: 1,
    hasNextPage: false,
    isLoading: false,
    isPageChanging: false,
    selectedMedicalRecord: selectMedicalRecordInitialState,
    showAdditionalInformationCard: false,
    hasNoMedicalRecords: false,
  };

  getFirstPage = () => {
    this.setState({ isLoading: true });

    if (this.props.patientId) {
      axios
        .post(
          getRequestUrl(
            `/api/patients/${this.props.patientId}/medical-records/page`
          ),
          {
            pageNumber: 1,
            pageSize: this.props.pageSize,
          },
          getAuthorizationConfig()
        )
        .then(response => {
          const medicalRecords = response.data.elements;
          const commonState = {
            medicalRecords: medicalRecords,
            hasNextPage: response.data.hasNext,
            isLoading: false,
          };

          if (medicalRecords.length === 0) {
            this.setState({
              ...commonState,
              hasNoMedicalRecords: true,
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
    } else {
      this.setState({ isLoading: false });
    }
  };

  componentDidMount() {
    this.getFirstPage();
  }

  componentDidUpdate(previousProps) {
    if (previousProps.patientId !== this.props.patientId) {
      this.getFirstPage();
    }
  }

  handlePageSelect = eventKey => {
    this.setState({
      isPageChanging: true,
      selectedMedicalRecord: selectMedicalRecordInitialState,
      showAdditionalInformationCard: false,
    });

    axios
      .post(
        getRequestUrl(
          `/api/patients/${this.props.patientId}/medical-records/page`
        ),
        {
          pageNumber: eventKey,
          pageSize: this.props.pageSize,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        this.setState({
          medicalRecords: response.data.elements,
          pageNumber: eventKey,
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
    this.setState({
      selectedMedicalRecord: this.state.medicalRecords.find(
        medicalRecord => medicalRecord.id === event.target.parentNode.id
      ),
      showAdditionalInformationCard: true,
    });
  };

  handleCloseButtonClick = () => {
    this.setState({
      selectedMedicalRecord: selectMedicalRecordInitialState,
      showAdditionalInformationCard: false,
    });
  };

  render() {
    return (
      <MedicalRecordListComponent
        medicalRecords={this.state.medicalRecords}
        pageNumber={this.state.pageNumber}
        hasNextPage={this.state.hasNextPage}
        isLoading={this.state.isLoading}
        isPageChanging={this.state.isPageChanging}
        selectedMedicalRecord={this.state.selectedMedicalRecord}
        showAdditionalInformationCard={this.state.showAdditionalInformationCard}
        hasNoMedicalRecords={this.state.hasNoMedicalRecords}
        noMedicalRecordsNotificationTitle={
          this.props.noMedicalRecordsNotificationTitle
        }
        noMedicalRecordsNotificationQuestion={
          this.props.noMedicalRecordsNotificationQuestion
        }
        handlePageSelect={this.handlePageSelect}
        handleTableClick={this.handleTableClick}
        handleCloseButtonClick={this.handleCloseButtonClick}
      />
    );
  }
}

export default MedicalRecordListContainer;
