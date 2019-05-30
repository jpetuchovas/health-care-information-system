import React, { Fragment } from 'react';
import { Col, Table } from 'react-bootstrap';

import AdministratorContactsComponent from '../../common/contacts/AdministratorContactsComponent';
import LoadingSpinner from '../../common/loading/LoadingSpinner';
import PaginationComponent from '../../common/pagination/PaginationComponent';
import MultipleConditionSearchComponent from '../../common/search/MultipleConditionSearchComponent';
import NoSearchResultsNotification from '../../common/search/NoSearchResultsNotification';
import './PatientListComponent.css';
import PatientListCsvDownloadButtonContainer from './PatientListCsvDownloadButtonContainer';

const styles = {
  hr: {
    margin: '7px 0',
  },

  noPatientsQuestion: {
    marginBottom: '5px',
  },

  tbody: {
    cursor: 'pointer',
  },
};

const PatientListComponent = ({
  searchQuery,
  submittedSearchQuery,
  patients,
  pageNumber,
  hasNextPage,
  isLoading,
  isPageChanging,
  hasNoPatients,
  handleFieldChange,
  handleSearchButtonClick,
  handlePageSelect,
  handleTableClick,
}) => (
  <Fragment>
    <MultipleConditionSearchComponent
      fieldAttributes={[
        {
          name: 'firstName',
          label: 'Vardas',
          placeholder: 'Vardas',
          value: searchQuery.firstName,
        },
        {
          name: 'lastName',
          label: 'Pavardė',
          placeholder: 'Pavardė',
          value: searchQuery.lastName,
        },
        {
          name: 'personalIdentificationNumber',
          label: 'Asmens kodas',
          placeholder: 'Asmens kodas',
          value: searchQuery.personalIdentificationNumber,
        },
        {
          name: 'diseaseCode',
          label: 'Ligos kodas',
          placeholder: 'Ligos kodas',
          value: searchQuery.diseaseCode,
        },
      ]}
      handleFieldChange={handleFieldChange}
      handleSubmit={handleSearchButtonClick}
    >
      {!hasNoPatients ? (
        <Col smOffset={4} sm={5}>
          <PatientListCsvDownloadButtonContainer />
        </Col>
      ) : null}
    </MultipleConditionSearchComponent>

    {!hasNoPatients ? <hr style={styles.hr} /> : null}

    <LoadingSpinner isLoading={isLoading} />

    {!isLoading && hasNoPatients ? (
      <div id="no-patients-notification">
        <h5>Dar neturite Jums priskirtų pacientų.</h5>
        <h6 style={styles.noPatientsQuestion}>
          Jūsų pacientų sąrašas neturėtų būti tuščias?
        </h6>
        <AdministratorContactsComponent />
      </div>
    ) : null}

    {!isLoading && !hasNoPatients && patients.length === 0 ? (
      <NoSearchResultsNotification>
        <h5>Pagal Jūsų įvestas paieškos frazes pacientų nebuvo rasta.</h5>
        <h6>
          Bandykite patikslinti paieškos frazes ir ieškoti dar kartą.<br />{' '}
          Rašymas didžiosiomis ar mažosiomis raidėmis paieškos rezultatų
          neįtakoja.<br />Paieškos frazėms taikoma dalinė atitiktis.
        </h6>
      </NoSearchResultsNotification>
    ) : null}

    {!isLoading && !hasNoPatients && patients.length > 0 ? (
      <Fragment>
        <h5>Pasirinkite pacientą</h5>
        <LoadingSpinner isLoading={isPageChanging} />

        {!isPageChanging ? (
          <Table condensed hover>
            <thead>
              <tr>
                <th>Vardas</th>
                <th>Pavardė</th>
                <th>Asmens kodas</th>
                <th>Gimimo data</th>
              </tr>
            </thead>

            <tbody onClick={handleTableClick} style={styles.tbody}>
              {patients.map(patient => (
                <tr id={patient.id} key={patient.id}>
                  <td>{patient.firstName}</td>
                  <td>{patient.lastName}</td>
                  <td>{patient.personalIdentificationNumber}</td>
                  <td>{patient.birthDate}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : null}

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

export default PatientListComponent;
