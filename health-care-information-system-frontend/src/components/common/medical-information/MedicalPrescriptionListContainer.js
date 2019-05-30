import axios from 'axios';
import PropTypes from 'prop-types';
import React, { Component } from 'react';

import { transformActiveIngredientQuantity } from '../../../common/dataTransformation';
import { logOut } from '../../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import MedicalPrescriptionListComponent from './MedicalPrescriptionListComponent';

const selectMedicalPrescriptionInitialState = {
  id: '',
  activeIngredient: '',
  activeIngredientQuantity: '',
  usageDescription: '',
  issueDate: '',
  hasUnlimitedValidity: null,
  validityEndDate: '',
  doctorFullName: '',
  purchaseFactCount: null,
  isValid: null,
};

class MedicalPrescriptionListContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  static propTypes = {
    patientId: PropTypes.string.isRequired,
    pageSize: PropTypes.number.isRequired,
    noMedicalPrescriptionsNotificationTitle: PropTypes.string.isRequired,
    noMedicalPrescriptionsNotificationQuestion: PropTypes.string.isRequired,
  };

  state = {
    medicalPrescriptions: [],
    pageNumber: 1,
    hasNextPage: false,
    isLoading: false,
    isPageChanging: false,
    selectedMedicalPrescription: selectMedicalPrescriptionInitialState,
    showAdditionalInformationCard: false,
    hasNoMedicalPrescriptions: false,
  };

  getFirstPage = () => {
    this.setState({ isLoading: true });

    if (this.props.patientId) {
      axios
        .post(
          getRequestUrl(
            `/api/patients/${this.props.patientId}/medical-prescriptions/page`
          ),
          {
            pageNumber: 1,
            pageSize: this.props.pageSize,
          },
          getAuthorizationConfig()
        )
        .then(response => {
          const medicalPrescriptions = transformActiveIngredientQuantity(
            response.data.elements
          );
          const commonState = {
            medicalPrescriptions: medicalPrescriptions,
            hasNextPage: response.data.hasNext,
            isLoading: false,
          };

          if (medicalPrescriptions.length === 0) {
            this.setState({
              ...commonState,
              hasNoMedicalPrescriptions: true,
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
      selectedMedicalPrescription: selectMedicalPrescriptionInitialState,
      showAdditionalInformationCard: false,
    });
    axios
      .post(
        getRequestUrl(
          `/api/patients/${this.props.patientId}/medical-prescriptions/page`
        ),
        {
          pageNumber: eventKey,
          pageSize: this.props.pageSize,
        },
        getAuthorizationConfig()
      )
      .then(response => {
        this.setState({
          medicalPrescriptions: transformActiveIngredientQuantity(
            response.data.elements
          ),
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
      selectedMedicalPrescription: this.state.medicalPrescriptions.find(
        medicalPrescription =>
          medicalPrescription.id === event.target.parentNode.id
      ),
      showAdditionalInformationCard: true,
    });
  };

  handleCloseButtonClick = () => {
    this.setState({
      selectedMedicalPrescription: selectMedicalPrescriptionInitialState,
      showAdditionalInformationCard: false,
    });
  };

  render() {
    return (
      <MedicalPrescriptionListComponent
        patientId={this.props.patientId}
        medicalPrescriptions={this.state.medicalPrescriptions}
        pageNumber={this.state.pageNumber}
        hasNextPage={this.state.hasNextPage}
        isLoading={this.state.isLoading}
        isPageChanging={this.state.isPageChanging}
        selectedMedicalPrescription={this.state.selectedMedicalPrescription}
        showAdditionalInformationCard={this.state.showAdditionalInformationCard}
        hasNoMedicalPrescriptions={this.state.hasNoMedicalPrescriptions}
        noMedicalPrescriptionsNotificationTitle={
          this.props.noMedicalPrescriptionsNotificationTitle
        }
        noMedicalPrescriptionsNotificationQuestion={
          this.props.noMedicalPrescriptionsNotificationQuestion
        }
        handlePageSelect={this.handlePageSelect}
        handleTableClick={this.handleTableClick}
        handleCloseButtonClick={this.handleCloseButtonClick}
      />
    );
  }
}

export default MedicalPrescriptionListContainer;
