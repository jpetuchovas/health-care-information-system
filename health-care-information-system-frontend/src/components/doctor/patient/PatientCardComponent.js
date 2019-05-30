import React, { Fragment } from 'react';
import { Button, Col, Glyphicon, Row, Tab, Tabs } from 'react-bootstrap';

import MedicalPrescriptionListContainer from '../../common/medical-information/MedicalPrescriptionListContainer';
import MedicalRecordListContainer from '../../common/medical-information/MedicalRecordListContainer';

const styles = {
  button: {
    marginBottom: '20px',
  },

  centerAlignedItems: {
    display: 'flex',
    alignItems: 'center',
  },

  glyphicon: {
    fontSize: '12em',
    marginRight: '20px',
  },

  tbody: {
    cursor: 'pointer',
  },
};

const PatientCardComponent = ({
  patient,
  pageSize,
  initialTab,
  handleTabSelect,
  handleNewMedicalRecordClick,
  handleNewMedicalPrescriptionClick,
}) => (
  <Fragment>
    <Row style={styles.centerAlignedItems}>
      <Col lg={2}>
        <Glyphicon glyph="user" style={styles.glyphicon} />
      </Col>

      <Col lg={10}>
        <p>
          <strong>Vardas:</strong> {patient.firstName}
        </p>
        <p>
          <strong>Pavardė:</strong> {patient.lastName}
        </p>
        <p>
          <strong>Asmens kodas:</strong> {patient.personalIdentificationNumber}
        </p>
        <p>
          <strong>Gimimo data:</strong> {patient.birthDate}
        </p>
      </Col>
    </Row>

    <Tabs
      defaultActiveKey={initialTab}
      id="patient-information"
      onSelect={handleTabSelect}
      justified
    >
      <Tab eventKey={1} title="Ligos istorija">
        <Button
          bsStyle="primary"
          style={styles.button}
          onClick={handleNewMedicalRecordClick}
        >
          Naujas ligos įrašas
        </Button>

        <MedicalRecordListContainer
          patientId={patient.id}
          pageSize={pageSize}
          noMedicalRecordsNotificationTitle="Pacientas dar neturi ligos istorijos įrašų."
          noMedicalRecordsNotificationQuestion="Paciento ligos istorija neturėtų būti tuščia?"
        />
      </Tab>

      <Tab eventKey={2} title="Išrašyti receptai">
        <Button
          bsStyle="primary"
          style={styles.button}
          onClick={handleNewMedicalPrescriptionClick}
        >
          Naujas receptas
        </Button>

        <MedicalPrescriptionListContainer
          patientId={patient.id}
          pageSize={pageSize}
          noMedicalPrescriptionsNotificationTitle="Pacientui dar nėra išrašytų receptų."
          noMedicalPrescriptionsNotificationQuestion="Paciento išrašytų receptų sąrašas neturėtų būti tuščias?"
        />
      </Tab>
    </Tabs>
  </Fragment>
);

export default PatientCardComponent;
