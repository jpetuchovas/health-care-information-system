import React, { Fragment } from 'react';
import { Col, Row, Table } from 'react-bootstrap';

import { LIGHT_BLUE_COLOR } from '../../../common/constants';
import AdministratorContactsComponent from '../contacts/AdministratorContactsComponent';
import LoadingSpinner from '../loading/LoadingSpinner';
import './MedicalInformationList.css';
import PaginationComponent from '../pagination/PaginationComponent';
import MedicalPrescriptionAdditionalInfoCard from './MedicalPrescriptionAdditionalInfoCard';

const styles = {
  col: {
    paddingRight: 0,
  },

  invalidPrescriptionRow: {
    opacity: 0.4,
  },

  noResultsQuestion: {
    marginBottom: '5px',
  },

  selectedRow: {
    color: LIGHT_BLUE_COLOR,
    fontWeight: 'bold',
  },

  tbody: {
    cursor: 'pointer',
  },
};

const MedicalPrescriptionListComponent = ({
  patientId,
  medicalPrescriptions,
  pageNumber,
  hasNextPage,
  isLoading,
  isPageChanging,
  selectedMedicalPrescription,
  showAdditionalInformationCard,
  hasNoMedicalPrescriptions,
  noMedicalPrescriptionsNotificationTitle,
  noMedicalPrescriptionsNotificationQuestion,
  handlePageSelect,
  handleTableClick,
  handleCloseButtonClick,
}) => (
  <Fragment>
    <LoadingSpinner isLoading={isLoading} />

    {!isLoading && hasNoMedicalPrescriptions ? (
      <div id="no-medical-prescriptions-notification">
        <h5>{noMedicalPrescriptionsNotificationTitle}</h5>
        <h6 style={styles.noResultsQuestion}>
          {noMedicalPrescriptionsNotificationQuestion}
        </h6>
        <AdministratorContactsComponent />
      </div>
    ) : null}

    {!isLoading && !hasNoMedicalPrescriptions ? (
      <Fragment>
        <h5>Pasirinkite receptą norėdami peržiūrėti detales</h5>

        {showAdditionalInformationCard ? (
          <Row>
            <Col sm={4} style={styles.col}>
              <Table condensed hover>
                <thead>
                  <tr>
                    <th>Galioja iki</th>
                    <th>Veiklioji medžiaga</th>
                  </tr>
                </thead>

                <tbody onClick={handleTableClick} style={styles.tbody}>
                  {medicalPrescriptions.map(medicalPrescription => (
                    <tr
                      id={medicalPrescription.id}
                      key={medicalPrescription.id}
                      className={medicalPrescription.isValid ? 'active' : null}
                      style={
                        medicalPrescription.isValid
                          ? medicalPrescription.id ===
                            selectedMedicalPrescription.id
                            ? styles.selectedRow
                            : null
                          : medicalPrescription.id ===
                            selectedMedicalPrescription.id
                            ? {
                                ...styles.selectedRow,
                                ...styles.invalidPrescriptionRow,
                              }
                            : styles.invalidPrescriptionRow
                      }
                    >
                      <td>
                        {medicalPrescription.hasUnlimitedValidity
                          ? 'Neterminuotai'
                          : medicalPrescription.validityEndDate}
                      </td>
                      <td>{medicalPrescription.activeIngredient}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Col>

            <Col sm={8} className="small-device-card">
              <MedicalPrescriptionAdditionalInfoCard
                patientId={patientId}
                selectedMedicalPrescription={selectedMedicalPrescription}
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
                    <th>Galioja iki</th>
                    <th>Veiklioji medžiaga</th>
                    <th>Veikliosios medžiagos kiekis</th>
                    <th>Panaudojimų skaičius</th>
                  </tr>
                </thead>

                <tbody onClick={handleTableClick} style={styles.tbody}>
                  {medicalPrescriptions.map(medicalPrescription => (
                    <tr
                      id={medicalPrescription.id}
                      key={medicalPrescription.id}
                      className={medicalPrescription.isValid ? 'active' : null}
                      style={
                        medicalPrescription.isValid
                          ? null
                          : styles.invalidPrescriptionRow
                      }
                    >
                      <td>
                        {medicalPrescription.hasUnlimitedValidity
                          ? 'Neterminuotai'
                          : medicalPrescription.validityEndDate}
                      </td>
                      <td>{medicalPrescription.activeIngredient}</td>
                      <td>{medicalPrescription.activeIngredientQuantity}</td>
                      <td>{medicalPrescription.purchaseFactCount}</td>
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
  </Fragment>
);

export default MedicalPrescriptionListComponent;
