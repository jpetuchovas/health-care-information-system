import React, { Fragment } from 'react';
import {
  Col,
  Row,
  Button,
  Glyphicon,
  Well,
  OverlayTrigger,
  Popover,
} from 'react-bootstrap';

import AssignmentPanel from './AssignmentPanel';
import { LIGHT_GREEN_COLOR, RED_COLOR } from '../../../common/constants';

const wellStyle = {
  margin: 0,
  padding: 0,
  textAlign: 'center',
  fontSize: '18px',
  minHeight: '35px',
  minWidth: '35px',
};

const popoverGlyphiconStyle = {
  fontSize: '20px',
  marginRight: '10px',
};

const styles = {
  doctorRowSelect: {
    backgroundColor: '#afd3d5',
  },

  patientRowSelect: {
    backgroundColor: '#e1bee7',
  },

  panelBody: {
    alignItems: 'center',
    display: 'flex',
    justifyContent: 'center',
  },

  patientWell: {
    ...wellStyle,
    backgroundColor: '#e1bee7',
  },

  doctorWell: {
    ...wellStyle,
    backgroundColor: '#afd3d5',
  },

  glyphiconArrow: {
    fontSize: '2.2em',
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

const PatientAssignmentComponent = ({
  doctorSearchQuery,
  patientSearchQuery,
  selectedDoctor,
  selectedPatient,
  doctors,
  patients,
  doctorPageNumber,
  patientPageNumber,
  doctorHasNextPage,
  patientHasNextPage,
  hasNotSearchedForDoctors,
  hasNotSearchedForPatients,
  existsNoDoctors,
  existsNoPatients,
  isDoctorSearchQueryLoading,
  isPatientSearchQueryLoading,
  isDoctorPageChanging,
  isPatientPageChanging,
  isDoctorPanelFocused,
  isPatientPanelFocused,
  handleFieldChange,
  handleSearchButtonClick,
  handlePageSelect,
  handleTableRowClick,
  handleDoctorComponentMouseEnter,
  handleDoctorComponentMouseLeave,
  handlePatientComponentMouseEnter,
  handlePatientComponentMouseLeave,
  handleSubmit,
  isSubmitting,
  isAssignmentSuccessful,
}) => (
  <Fragment>
    <Row>
      <Col xs={12} md={6}>
        <AssignmentPanel
          panelTitle="Pasirinkite gydytoją"
          fieldAttributes={[
            {
              name: 'firstName',
              label: 'Vardas',
              placeholder: 'Vardas',
              value: doctorSearchQuery.firstName,
            },
            {
              name: 'lastName',
              label: 'Pavardė',
              placeholder: 'Pavardė',
              value: doctorSearchQuery.lastName,
            },
            {
              name: 'specialization',
              label: 'Specializacija',
              placeholder: 'Specializacija',
              value: doctorSearchQuery.specialization,
            },
            {
              name: 'username',
              label: 'Vartotojo vardas',
              placeholder: 'Vartotojo vardas',
              value: doctorSearchQuery.username,
            },
          ]}
          users={doctors}
          selectedUser={selectedDoctor}
          thirdAttributeTitle="Specializacija"
          thirdAttributeName="specialization"
          pageNumber={doctorPageNumber}
          hasNextPage={doctorHasNextPage}
          handleFieldChange={handleFieldChange('doctorSearchQuery')}
          handleSubmit={handleSearchButtonClick('doctors')}
          handlePageSelect={handlePageSelect('doctors')}
          handleTableRowClick={handleTableRowClick('selectedDoctor')}
          handleMouseEnter={handleDoctorComponentMouseEnter}
          handleMouseLeave={handleDoctorComponentMouseLeave}
          rowSelectStyle={styles.doctorRowSelect}
          isSearchQueryLoading={isDoctorSearchQueryLoading}
          isPageChanging={isDoctorPageChanging}
          isPanelFocused={isDoctorPanelFocused}
          isDisabled={isSubmitting}
          userAddress="gydytojų"
          existsNoUsers={existsNoDoctors}
          hasNotSearchedFor={hasNotSearchedForDoctors}
        />
      </Col>

      <Col xs={12} md={6}>
        <AssignmentPanel
          panelTitle="Pasirinkite pacientą"
          fieldAttributes={[
            {
              name: 'firstName',
              label: 'Vardas',
              placeholder: 'Vardas',
              value: patientSearchQuery.firstName,
            },
            {
              name: 'lastName',
              label: 'Pavardė',
              placeholder: 'Pavardė',
              value: patientSearchQuery.lastName,
            },
            {
              name: 'personalIdentificationNumber',
              label: 'Asmens kodas',
              placeholder: 'Asmens kodas',
              value: patientSearchQuery.personalIdentificationNumber,
            },
            {
              name: 'username',
              label: 'Vartotojo vardas',
              placeholder: 'Vartotojo vardas',
              value: patientSearchQuery.username,
            },
          ]}
          users={patients}
          selectedUser={selectedPatient}
          thirdAttributeTitle="Asmens kodas"
          thirdAttributeName="personalIdentificationNumber"
          pageNumber={patientPageNumber}
          hasNextPage={patientHasNextPage}
          handleFieldChange={handleFieldChange('patientSearchQuery')}
          handleSubmit={handleSearchButtonClick('patients')}
          handlePageSelect={handlePageSelect('patients')}
          handleTableRowClick={handleTableRowClick('selectedPatient')}
          handleMouseEnter={handlePatientComponentMouseEnter}
          handleMouseLeave={handlePatientComponentMouseLeave}
          rowSelectStyle={styles.patientRowSelect}
          isSearchQueryLoading={isPatientSearchQueryLoading}
          isPageChanging={isPatientPageChanging}
          isPanelFocused={isPatientPanelFocused}
          isDisabled={isSubmitting}
          userAddress="pacientų"
          existsNoUsers={existsNoPatients}
          hasNotSearchedFor={hasNotSearchedForPatients}
        />
      </Col>
    </Row>

    <Row>
      <Col sm={10} smOffset={1}>
        <div className="panel panel-default">
          <div className="panel-body" style={styles.panelBody}>
            <Col sm={5}>
              <Well style={styles.doctorWell}>
                {`${selectedDoctor.firstName} ${selectedDoctor.lastName}`}
              </Well>
            </Col>

            <Glyphicon
              glyph="resize-horizontal"
              bsSize="large"
              style={styles.glyphiconArrow}
            />

            <Col sm={5}>
              <Well style={styles.patientWell}>
                {`${selectedPatient.firstName} ${selectedPatient.lastName}`}
              </Well>
            </Col>

            <OverlayTrigger
              rootClose={true}
              trigger="click"
              placement="top"
              overlay={
                isAssignmentSuccessful ? (
                  <Popover id="submit-popover">
                    <Glyphicon glyph="ok" style={styles.successGlyphicon} />
                    <span className="text-success">Priskyrimas sėkmingas.</span>
                  </Popover>
                ) : (
                  <Popover id="submit-popover">
                    <Glyphicon glyph="alert" style={styles.errorGlyphicon} />
                    <span className="text-danger">
                      Įvyko klaida siunčiant užklausą į serverį. Bandykite dar
                      kartą
                    </span>
                  </Popover>
                )
              }
            >
              <Button
                bsStyle="primary"
                disabled={
                  !(selectedDoctor.id && selectedPatient.id) || isSubmitting
                }
                onClick={handleSubmit}
              >
                {isSubmitting ? 'Priskiriama...' : 'Priskirti'}
              </Button>
            </OverlayTrigger>
          </div>
        </div>
      </Col>
      <Col sm={1} />
    </Row>
  </Fragment>
);

export default PatientAssignmentComponent;
