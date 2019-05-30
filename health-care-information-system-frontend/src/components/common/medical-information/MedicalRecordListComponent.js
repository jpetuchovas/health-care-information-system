import React, { Fragment } from 'react';
import { Col, Row, Table } from 'react-bootstrap';

import CloseButton from '../closing/CloseButton';
import { LIGHT_BLUE_COLOR } from '../../../common/constants';
import { convertMinutesToTextWithHours } from '../../../common/dataTransformation';
import AdministratorContactsComponent from '../contacts/AdministratorContactsComponent';
import LoadingSpinner from '../loading/LoadingSpinner';
import './MedicalInformationList.css';
import PaginationComponent from '../pagination/PaginationComponent';

const styles = {
  col: {
    paddingRight: 0,
  },

  noMedicalRecordsQuestion: {
    marginBottom: '5px',
  },

  panel: {
    marginLeft: '15px',
  },

  panelBody: {
    maxHeight: '400px',
    minHeight: '400px',
    overflowY: 'auto',
  },

  selectedRow: {
    color: LIGHT_BLUE_COLOR,
    fontWeight: 'bold',
  },

  spacedElements: {
    display: 'flex',
    justifyContent: 'space-between',
  },

  additionalInfoCardTable: {
    marginBottom: '5px',
  },

  tbody: {
    cursor: 'pointer',
  },
};

const MedicalRecordListComponent = ({
  medicalRecords,
  pageNumber,
  hasNextPage,
  isLoading,
  isPageChanging,
  selectedMedicalRecord,
  showAdditionalInformationCard,
  hasNoMedicalRecords,
  noMedicalRecordsNotificationTitle,
  noMedicalRecordsNotificationQuestion,
  handlePageSelect,
  handleTableClick,
  handleCloseButtonClick,
}) => (
  <Fragment>
    <LoadingSpinner isLoading={isLoading} />

    {!isLoading && hasNoMedicalRecords ? (
      <div id="no-medical-records-notification">
        <h5>{noMedicalRecordsNotificationTitle}</h5>
        <h6 style={styles.noMedicalRecordsQuestion}>
          {noMedicalRecordsNotificationQuestion}
        </h6>
        <AdministratorContactsComponent />
      </div>
    ) : null}

    {!isLoading && !hasNoMedicalRecords ? (
      <Fragment>
        <h5>Pasirinkite ligos istorijos įrašą norėdami peržiūrėti detales</h5>

        {showAdditionalInformationCard ? (
          <Row>
            <Col sm={4} style={styles.col}>
              <Table condensed hover>
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Ligos kodas</th>
                  </tr>
                </thead>

                <tbody onClick={handleTableClick} style={styles.tbody}>
                  {medicalRecords.map(medicalRecord => (
                    <tr
                      id={medicalRecord.id}
                      key={medicalRecord.id}
                      style={
                        medicalRecord.id === selectedMedicalRecord.id
                          ? styles.selectedRow
                          : null
                      }
                    >
                      <td>{medicalRecord.date}</td>
                      <td>{medicalRecord.diseaseCode}</td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Col>

            <Col sm={8} className="small-device-card">
              <div className="panel panel-default" style={styles.panel}>
                <div className="panel-body" style={styles.panelBody}>
                  <div style={styles.spacedElements}>
                    <CloseButton handleClose={handleCloseButtonClick} />
                    <span>{selectedMedicalRecord.date}</span>
                  </div>

                  <div style={styles.spacedElements}>
                    <h4 className="text-primary">
                      {selectedMedicalRecord.diseaseCode}
                    </h4>
                    <h4 className="text-muted">
                      Gydytojas {selectedMedicalRecord.doctorFullName}
                    </h4>
                  </div>

                  <Table condensed style={styles.additionalInfoCardTable}>
                    <thead>
                      <tr>
                        <td>Ar vizitas kompensuojas?</td>
                        <td>Ar vizitas pakartotinis?</td>
                        <td>Vizito trukmė</td>
                      </tr>
                    </thead>

                    <tbody>
                      <tr>
                        <td>
                          {selectedMedicalRecord.isVisitCompensated
                            ? 'Taip'
                            : 'Ne'}
                        </td>
                        <td>
                          {selectedMedicalRecord.isVisitRepeated
                            ? 'Taip'
                            : 'Ne'}
                        </td>
                        <td>
                          {convertMinutesToTextWithHours(
                            selectedMedicalRecord.visitDurationInMinutes
                          )}
                        </td>
                      </tr>
                    </tbody>
                  </Table>

                  <h5>Aprašymas</h5>
                  <p className="text-justify">
                    {selectedMedicalRecord.description}
                  </p>
                </div>
              </div>
            </Col>
          </Row>
        ) : (
          <Fragment>
            <LoadingSpinner isLoading={isPageChanging} />
            {!isPageChanging ? (
              <Table condensed hover>
                <thead>
                  <tr>
                    <th>Data</th>
                    <th>Ligos kodas</th>
                    <th>Gydytojas</th>
                  </tr>
                </thead>

                <tbody style={styles.tbody}>
                  {medicalRecords.map(medicalRecord => (
                    <tr
                      id={medicalRecord.id}
                      key={medicalRecord.id}
                      onClick={handleTableClick}
                    >
                      <td>{medicalRecord.date}</td>
                      <td>{medicalRecord.diseaseCode}</td>
                      <td>{medicalRecord.doctorFullName}</td>
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

export default MedicalRecordListComponent;
