import React, { Fragment } from 'react';
import {
  Badge,
  Button,
  Checkbox,
  Col,
  FormControl,
  FormGroup,
  HelpBlock,
  InputGroup,
  Navbar,
  Row,
  Table,
} from 'react-bootstrap';

import {
  LIGHT_BLUE_COLOR,
  LIGHT_GREEN_COLOR,
  RED_COLOR,
} from '../../common/constants';
import AdministratorContactsComponent from '../common/contacts/AdministratorContactsComponent';
import SubmitAlert from '../common/form/alert/SubmitAlert';
import UnknownErrorSubmitAlert from '../common/form/alert/UnknownErrorSubmitAlert';
import LoadingSpinner from '../common/loading/LoadingSpinner';
import '../common/medical-information/MedicalInformationList.css';
import MedicalPrescriptionAdditionalInfoCard from '../common/medical-information/MedicalPrescriptionAdditionalInfoCard';
import PaginationComponent from '../common/pagination/PaginationComponent';
import NoSearchResultsNotification from '../common/search/NoSearchResultsNotification';
import SearchButton from '../common/search/SearchButton';
import './SearchableMedicalPrescriptionListComponent.css';

const badgeStyle = {
  marginRight: '5px',
};

const badgeTextStyle = {
  width: '170px',
};

const popoverGlyphiconStyle = {
  fontSize: '20px',
  marginRight: '10px',
};

const BADGE_COLOR = '#8bc34a';

const styles = {
  badgeDefault: badgeStyle,

  badgeDefaultText: badgeTextStyle,

  badgeSuccess: {
    ...badgeStyle,
    backgroundColor: BADGE_COLOR,
  },

  badgeSuccessText: {
    ...badgeTextStyle,
    color: BADGE_COLOR,
  },

  checkbox: {
    marginBottom: '1px',
    marginLeft: '5px',
    marginTop: '2px',
  },

  col: {
    paddingRight: 0,
  },

  footer: {
    backgroundColor: '#f5f5f5',
    paddingLeft: '41px',
  },

  markMedicalPrescriptionsAsUsedButton: {
    marginRight: '30px',
    width: '185px',
  },

  noResultsQuestion: {
    marginBottom: '5px',
  },

  patientInformationHeading: {
    marginBottom: '3px',
    marginTop: 0,
  },

  searchInput: {
    marginRight: '10px',
    width: '300px',
  },

  searchInputHeading: {
    marginBottom: '5px',
  },

  selectedForMoreInformationRow: {
    color: LIGHT_BLUE_COLOR,
    fontWeight: 'bold',
  },

  spacedElements: {
    display: 'flex',
    justifyContent: 'space-between',
  },

  tbody: {
    cursor: 'pointer',
  },

  checkboxTd: {
    cursor: 'default',
    padding: '0',
    textAlign: 'center',
    verticalAlign: 'middle',
    width: '40px',
  },

  successGlyphicon: {
    ...popoverGlyphiconStyle,
    color: LIGHT_GREEN_COLOR,
  },

  errorGlyphicon: {
    ...popoverGlyphiconStyle,
    color: RED_COLOR,
  },
};

const getSelectedPrescriptionsCountText = selectedPrescriptionsCount => {
  if (
    (selectedPrescriptionsCount % 100 >= 11 &&
      selectedPrescriptionsCount % 100 <= 19) ||
    selectedPrescriptionsCount % 10 === 0
  ) {
    return 'pažymėtų receptų';
  } else if (selectedPrescriptionsCount % 10 === 1) {
    return 'pažymėtas receptas';
  } else {
    return 'pažymėti receptai';
  }
};

const SearchableMedicalPrescriptionListComponent = ({
  personalIdentificationNumber,
  submittedPersonalIdentificationNumber,
  errorMessage,
  patient,
  medicalPrescriptions,
  selectedMedicalPrescriptionIds,
  selectedMedicalPrescriptionsCount,
  pageNumber,
  hasNextPage,
  selectedForMoreInfoMedicalPrescription,
  showAdditionalInformationCard,
  isLoading,
  isPageChanging,
  isSubmitting,
  isSubmitSuccessful,
  isAlertVisible,
  handleFieldChange,
  handleSearchButtonClick,
  handlePageSelect,
  handleCheckboxClick,
  handleTableClick,
  handleCloseButtonClick,
  handleCancelSelectActionButtonClick,
  handleSubmit,
  handleAlertDismiss,
}) => (
  <Fragment>
    {!isSubmitSuccessful && isAlertVisible ? (
      <UnknownErrorSubmitAlert handleDismiss={handleAlertDismiss} />
    ) : null}

    <Row>
      <Col sm={6}>
        <h5 style={styles.searchInputHeading}>Įveskite paciento asmens kodą</h5>
        <div>
          <FormGroup
            controlId="personalIdentificationNumber"
            validationState={errorMessage ? 'error' : null}
          >
            <InputGroup>
              <FormControl
                type="text"
                placeholder="Asmens kodas"
                value={personalIdentificationNumber}
                style={styles.searchInput}
                onChange={handleFieldChange}
                onKeyPress={event => {
                  if (event.key === 'Enter') {
                    handleSearchButtonClick(event);
                  }
                }}
                disabled={isSubmitting}
              />

              <SearchButton
                handleClick={handleSearchButtonClick}
                isDisabled={isSubmitting}
              />
            </InputGroup>

            {errorMessage ? <HelpBlock>{errorMessage}</HelpBlock> : null}
          </FormGroup>
        </div>
      </Col>

      <Col smOffset={3} sm={3}>
        {!isLoading && submittedPersonalIdentificationNumber && patient.id ? (
          <div className="right-aligned-text">
            <h6 style={styles.patientInformationHeading}>
              Informacija apie pacientą
            </h6>

            <p>
              <strong>Vardas:</strong>{' '}
              {`${patient.firstName} ${patient.lastName}`}
              <br />
              <strong>Asmens kodas:</strong>{' '}
              {patient.personalIdentificationNumber}
              <br />
              <strong>Gimimo data:</strong> {patient.birthDate}
            </p>
          </div>
        ) : null}
      </Col>
    </Row>

    {isSubmitSuccessful && isAlertVisible ? (
      <SubmitAlert
        type="success"
        text="Pirkimo faktai sėkmingai pažymėti."
        handleDismiss={handleAlertDismiss}
      />
    ) : null}

    <LoadingSpinner isLoading={isLoading} />

    {!isLoading && submittedPersonalIdentificationNumber && !patient.id ? (
      <NoSearchResultsNotification>
        <h5>Paciento su tokiu asmens kodu nebuvo rasta.</h5>
        <h6 style={styles.noResultsQuestion}>
          Pacientas su tokiu asmens kodu turėtų egzistuoti „Medika“ sistemoje?
        </h6>
        <AdministratorContactsComponent />
      </NoSearchResultsNotification>
    ) : null}

    {!isLoading &&
    submittedPersonalIdentificationNumber &&
    patient.id &&
    medicalPrescriptions.length === 0 ? (
      <div id="no-medical-prescriptions-notification">
        <h5>Pacientui dar nėra išrašytų receptų.</h5>
        <h6 style={styles.noResultsQuestion}>
          Paciento išrašytų receptų sąrašas neturėtų būti tuščias?
        </h6>
        <AdministratorContactsComponent />
      </div>
    ) : null}

    {!isLoading &&
    submittedPersonalIdentificationNumber &&
    patient.id &&
    medicalPrescriptions.length > 0 ? (
      <Fragment>
        <hr />
        <h6>
          Varnele pažymėkite perkamų vaistų receptus arba pasirinkite receptą
          norėdami peržiūrėti detales.
        </h6>

        {showAdditionalInformationCard ? (
          <Row>
            <Col sm={4} style={styles.col}>
              <Table condensed hover>
                <thead>
                  <tr>
                    <th />
                    <th>Galioja iki</th>
                    <th>Veiklioji medžiaga</th>
                  </tr>
                </thead>

                <tbody style={styles.tbody}>
                  {medicalPrescriptions.map(medicalPrescription => (
                    <tr
                      id={medicalPrescription.id}
                      key={medicalPrescription.id}
                      className={
                        selectedMedicalPrescriptionIds.includes(
                          medicalPrescription.id
                        )
                          ? 'success'
                          : null
                      }
                      style={
                        medicalPrescription.id ===
                        selectedForMoreInfoMedicalPrescription.id
                          ? styles.selectedForMoreInformationRow
                          : null
                      }
                    >
                      <td style={styles.checkboxTd} className="green-checkbox">
                        <Checkbox
                          id={medicalPrescription.id}
                          style={styles.checkbox}
                          onClick={handleCheckboxClick}
                          checked={selectedMedicalPrescriptionIds.includes(
                            medicalPrescription.id
                          )}
                          readOnly
                        />
                      </td>
                      <td onClick={handleTableClick}>
                        {medicalPrescription.hasUnlimitedValidity
                          ? 'Neterminuotai'
                          : medicalPrescription.validityEndDate}
                      </td>
                      <td onClick={handleTableClick}>
                        {medicalPrescription.activeIngredient}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Col>

            <Col sm={8} className="small-device-card">
              <MedicalPrescriptionAdditionalInfoCard
                selectedMedicalPrescription={
                  selectedForMoreInfoMedicalPrescription
                }
                handleCloseButtonClick={handleCloseButtonClick}
              />
            </Col>
          </Row>
        ) : (
          <Fragment>
            <LoadingSpinner isLoading={isPageChanging} />

            {!isPageChanging ? (
              <Table condensed hover>
                <thead>
                  <tr>
                    <th />
                    <th>Galioja iki</th>
                    <th>Veiklioji medžiaga</th>
                    <th>Veikliosios medžiagos kiekis</th>
                  </tr>
                </thead>

                <tbody style={styles.tbody}>
                  {medicalPrescriptions.map(medicalPrescription => (
                    <tr
                      id={medicalPrescription.id}
                      key={medicalPrescription.id}
                      className={
                        selectedMedicalPrescriptionIds.includes(
                          medicalPrescription.id
                        )
                          ? 'success'
                          : null
                      }
                    >
                      <td style={styles.checkboxTd} className="green-checkbox">
                        <Checkbox
                          id={medicalPrescription.id}
                          style={styles.checkbox}
                          onClick={handleCheckboxClick}
                          checked={selectedMedicalPrescriptionIds.includes(
                            medicalPrescription.id
                          )}
                          readOnly
                        />
                      </td>
                      <td onClick={handleTableClick}>
                        {medicalPrescription.hasUnlimitedValidity
                          ? 'Neterminuotai'
                          : medicalPrescription.validityEndDate}
                      </td>
                      <td onClick={handleTableClick}>
                        {medicalPrescription.activeIngredient}
                      </td>
                      <td onClick={handleTableClick}>
                        {medicalPrescription.activeIngredientQuantity}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : null}
          </Fragment>
        )}

        <PaginationComponent
          activePageNumber={pageNumber}
          hasNextPage={hasNextPage}
          isPageChanging={isPageChanging}
          handlePageSelect={handlePageSelect}
          marginBottom="20px"
        />
      </Fragment>
    ) : null}

    {!isLoading &&
    submittedPersonalIdentificationNumber &&
    patient.id &&
    medicalPrescriptions.length > 0 ? (
      <Navbar
        componentClass="footer"
        className="footer"
        fluid
        fixedBottom
        style={styles.footer}
      >
        <Navbar.Text
          style={
            selectedMedicalPrescriptionsCount > 0
              ? styles.badgeSuccessText
              : styles.badgeDefaultText
          }
        >
          <Badge
            style={
              selectedMedicalPrescriptionsCount > 0
                ? styles.badgeSuccess
                : styles.badgeDefault
            }
          >
            {selectedMedicalPrescriptionsCount}
          </Badge>
          {getSelectedPrescriptionsCountText(selectedMedicalPrescriptionsCount)}
        </Navbar.Text>

        <Navbar.Form>
          <Button
            bsStyle="primary"
            onClick={handleSubmit}
            style={styles.markMedicalPrescriptionsAsUsedButton}
            disabled={selectedMedicalPrescriptionsCount === 0 || isSubmitting}
          >
            {selectedMedicalPrescriptionsCount > 1
              ? 'Žymėti pirkimo faktus'
              : 'Žymėti pirkimo faktą'}
          </Button>

          <Button
            bsStyle="warning"
            onClick={handleCancelSelectActionButtonClick}
            style={styles.resetButton}
            disabled={selectedMedicalPrescriptionsCount === 0 || isSubmitting}
          >
            Atšaukti pasirinkimus
          </Button>
        </Navbar.Form>
      </Navbar>
    ) : null}
  </Fragment>
);

export default SearchableMedicalPrescriptionListComponent;
