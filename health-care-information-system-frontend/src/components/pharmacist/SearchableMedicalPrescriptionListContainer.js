import PropTypes from 'prop-types';
import axios from 'axios';
import React, { Component } from 'react';

import { PERSONAL_IDENTIFICATION_NUMBER_LENGTH } from '../../common/constants';
import { transformActiveIngredientQuantity } from '../../common/dataTransformation';
import { logOut } from '../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../common/requestUtils';
import {
  PERSONAL_IDENTIFICATION_NUMBER_SEARCH_PATTERN,
  PERSONAL_IDENTIFICATION_NUMBER_START_SEARCH_PATTERN,
} from '../../common/validation';
import SearchableMedicalPrescriptionListComponent from './SearchableMedicalPrescriptionListComponent';

const styles = {
  body: {
    marginBottom: '165px',
  },
};

const PAGE_SIZE = 5;

const getPersonalIdentificationNumberErrorMessage = personalIdentificationNumber => {
  if (!personalIdentificationNumber) {
    return 'Įveskite paciento asmens kodą.';
  } else if (
    !PERSONAL_IDENTIFICATION_NUMBER_SEARCH_PATTERN.test(
      personalIdentificationNumber
    )
  ) {
    return 'Asmens kodas turi būti sudarytas tik iš skaitmenų.';
  } else if (
    !PERSONAL_IDENTIFICATION_NUMBER_START_SEARCH_PATTERN.test(
      personalIdentificationNumber
    )
  ) {
    return 'Asmens kodas turi prasidėti skaitmeniu 3, 4, 5 arba 6.';
  } else if (
    personalIdentificationNumber.trim().length !==
    PERSONAL_IDENTIFICATION_NUMBER_LENGTH
  ) {
    return `Asmens kodas turi būti sudarytas iš ${PERSONAL_IDENTIFICATION_NUMBER_LENGTH} skaitmenų.`;
  } else {
    return '';
  }
};

const initialPatientState = {
  id: '',
  firstName: '',
  lastName: '',
  personalIdentificationNumber: '',
  birthDate: '',
};

const selectedForMoreInfoMedicalPrescriptionInitialState = {
  id: '',
  activeIngredient: '',
  activeIngredientQuantity: '',
  usageDescription: '',
  issueDate: '',
  hasUnlimitedValidity: null,
  validityEndDate: '',
  doctorFullName: '',
};

const initialState = {
  personalIdentificationNumber: '',
  submittedPersonalIdentificationNumber: '',
  errorMessage: '',
  patient: initialPatientState,
  medicalPrescriptions: [],
  selectedMedicalPrescriptionIds: [],
  selectedMedicalPrescriptionsCount: 0,
  pageNumber: 1,
  hasNextPage: false,
  selectedForMoreInfoMedicalPrescription: selectedForMoreInfoMedicalPrescriptionInitialState,
  showAdditionalInformationCard: false,
  isLoading: false,
  isPageChanging: false,
  isSubmitting: false,
  isSubmitSuccessful: true,
  isAlertVisible: false,
};

class SearchableMedicalPrescriptionListContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  state = initialState;

  componentDidMount() {
    document.body.style.marginBottom = styles.body.marginBottom;
  }

  componentWillUnmount() {
    document.body.style.marginBottom = null;
  }

  handleFieldChange = event => {
    this.setState({
      personalIdentificationNumber: event.target.value,
      isAlertVisible: false,
    });
  };

  handleSearchButtonClick = event => {
    const personalIdentificationNumber = this.state
      .personalIdentificationNumber;
    const errorMessage = getPersonalIdentificationNumberErrorMessage(
      personalIdentificationNumber
    );
    const commonPayload = {
      errorMessage,
      selectedMedicalPrescriptionIds: [],
      selectedMedicalPrescriptionsCount: 0,
      pageNumber: 1,
    };

    if (errorMessage) {
      this.setState({
        ...commonPayload,
        submittedPersonalIdentificationNumber: '',
        patient: initialPatientState,
        medicalPrescriptions: [],
        hasNextPage: false,
        isLoading: false,
      });
    } else {
      event.preventDefault();

      this.setState({ isLoading: true });

      axios
        .post(
          getRequestUrl('/api/patients/filter/personal-identification-number'),
          {
            personalIdentificationNumber,
          },
          getAuthorizationConfig()
        )
        .then(response => {
          const patient = response.data;

          if (patient.id) {
            axios
              .post(
                getRequestUrl(
                  `/api/patients/${patient.id}/medical-prescriptions/valid`
                ),
                {
                  pageNumber: 1,
                  pageSize: PAGE_SIZE,
                },
                getAuthorizationConfig()
              )
              .then(response => {
                this.setState({
                  ...commonPayload,
                  patient,
                  submittedPersonalIdentificationNumber: personalIdentificationNumber,
                  medicalPrescriptions: transformActiveIngredientQuantity(
                    response.data.elements
                  ),
                  selectedMedicalPrescriptionIds: [],
                  hasNextPage: response.data.hasNext,
                  isLoading: false,
                });
              })
              .catch(error => {
                if (error.response && error.response.status === 401) {
                  logOut();
                  this.context.router.push('/login');
                } else {
                  this.setState({
                    ...commonPayload,
                    patient,
                    submittedPersonalIdentificationNumber: personalIdentificationNumber,
                    medicalPrescriptions: [],
                    hasNextPage: false,
                    isLoading: false,
                  });
                }
              });
          } else {
            this.setState({
              ...commonPayload,
              patient: initialPatientState,
              submittedPersonalIdentificationNumber: personalIdentificationNumber,
              medicalPrescriptions: [],
              hasNextPage: false,
              isLoading: false,
            });
          }
        })
        .catch(error => {
          if (error.response && error.response.status === 401) {
            logOut();
            this.context.router.push('/login');
          } else {
            this.setState({
              ...commonPayload,
              submittedPersonalIdentificationNumber: '',
              patient: initialPatientState,
              medicalPrescriptions: [],
              hasNextPage: false,
              isLoading: false,
            });
          }
        });
    }
  };

  handlePageSelect = eventKey => {
    this.setState({
      isPageChanging: true,
      selectedForMoreInfoMedicalPrescription: selectedForMoreInfoMedicalPrescriptionInitialState,
      showAdditionalInformationCard: false,
      personalIdentificationNumber: this.state
        .submittedPersonalIdentificationNumber,
    });

    axios
      .post(
        getRequestUrl(
          `/api/patients/${this.state.patient.id}/medical-prescriptions/valid`
        ),
        {
          pageNumber: eventKey,
          pageSize: PAGE_SIZE,
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

  handleCheckboxClick = event => {
    const prescriptionId =
      event.target.parentNode.parentNode.parentNode.parentNode.id;

    if (event.target.checked) {
      this.setState({
        selectedMedicalPrescriptionIds: [
          ...this.state.selectedMedicalPrescriptionIds,
          prescriptionId,
        ],
        selectedMedicalPrescriptionsCount:
          this.state.selectedMedicalPrescriptionsCount + 1,
      });
    } else {
      this.setState({
        selectedMedicalPrescriptionIds: this.state.selectedMedicalPrescriptionIds.filter(
          selectedPrescriptionId => selectedPrescriptionId !== prescriptionId
        ),
        selectedMedicalPrescriptionsCount:
          this.state.selectedMedicalPrescriptionsCount - 1,
      });
    }
  };

  handleTableClick = event => {
    this.setState({
      selectedForMoreInfoMedicalPrescription: this.state.medicalPrescriptions.find(
        medicalPrescription =>
          medicalPrescription.id === event.target.parentNode.id
      ),
      showAdditionalInformationCard: true,
    });
  };

  handleCloseButtonClick = () => {
    this.setState({
      selectedForMoreInfoMedicalPrescription: selectedForMoreInfoMedicalPrescriptionInitialState,
      showAdditionalInformationCard: false,
    });
  };

  handleCancelSelectActionButtonClick = () => {
    this.setState({
      selectedMedicalPrescriptionIds: [],
      selectedMedicalPrescriptionsCount: 0,
    });
  };

  handleSubmit = event => {
    event.preventDefault();

    this.setState({ isSubmitting: true });

    axios
      .put(
        getRequestUrl(
          `/api/patients/${this.state.patient.id}/medical-prescriptions`
        ),
        this.state.selectedMedicalPrescriptionIds,
        getAuthorizationConfig()
      )
      .then(() => {
        this.setState({
          ...initialState,
          isSubmitting: false,
          isSubmitSuccessful: true,
          isAlertVisible: true,
        });
      })
      .catch(() => {
        this.setState({
          isSubmitting: false,
          isSubmitSuccessful: false,
          isAlertVisible: true,
        });
      });
  };

  handleAlertDismiss = () => {
    this.setState({ isAlertVisible: false });
  };

  render() {
    return (
      <SearchableMedicalPrescriptionListComponent
        personalIdentificationNumber={this.state.personalIdentificationNumber}
        submittedPersonalIdentificationNumber={
          this.state.submittedPersonalIdentificationNumber
        }
        errorMessage={this.state.errorMessage}
        patient={this.state.patient}
        medicalPrescriptions={this.state.medicalPrescriptions}
        selectedMedicalPrescriptionIds={
          this.state.selectedMedicalPrescriptionIds
        }
        selectedMedicalPrescriptionsCount={
          this.state.selectedMedicalPrescriptionsCount
        }
        pageNumber={this.state.pageNumber}
        hasNextPage={this.state.hasNextPage}
        selectedForMoreInfoMedicalPrescription={
          this.state.selectedForMoreInfoMedicalPrescription
        }
        showAdditionalInformationCard={this.state.showAdditionalInformationCard}
        isLoading={this.state.isLoading}
        isPageChanging={this.state.isPageChanging}
        isSubmitting={this.state.isSubmitting}
        isSubmitSuccessful={this.state.isSubmitSuccessful}
        isAlertVisible={this.state.isAlertVisible}
        handleFieldChange={this.handleFieldChange}
        handleSearchButtonClick={this.handleSearchButtonClick}
        handlePageSelect={this.handlePageSelect}
        handleCheckboxClick={this.handleCheckboxClick}
        handleTableClick={this.handleTableClick}
        handleCloseButtonClick={this.handleCloseButtonClick}
        handleCancelSelectActionButtonClick={
          this.handleCancelSelectActionButtonClick
        }
        handleSubmit={this.handleSubmit}
        handleAlertDismiss={this.handleAlertDismiss}
      />
    );
  }
}

export default SearchableMedicalPrescriptionListContainer;
