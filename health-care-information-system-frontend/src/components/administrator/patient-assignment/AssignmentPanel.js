import React, { Fragment } from 'react';
import { Table } from 'react-bootstrap';

import MultipleConditionSearchComponent from '../../common/search/MultipleConditionSearchComponent';
import LoadingSpinner from '../../common/loading/LoadingSpinner';
import PaginationComponent from '../../common/pagination/PaginationComponent';
import NoSearchResultsNotification from '../../common/search/NoSearchResultsNotification';

const panelStyle = {
  marginBottom: '10px',
  minHeight: '490px',
};

const styles = {
  tbody: {
    cursor: 'pointer',
  },

  unfocusedPanel: {
    ...panelStyle,
    opacity: 0.3,
  },

  focusedPanel: {
    ...panelStyle,
    opacity: 1,
  },

  panelBody: {
    paddingBottom: '10px',
  },
};

const AssignmentPanel = ({
  panelTitle,
  fieldAttributes,
  users,
  selectedUser,
  thirdAttributeTitle,
  thirdAttributeName,
  pageNumber,
  hasNextPage,
  handleFieldChange,
  handleSubmit,
  handlePageSelect,
  handleTableRowClick,
  handleMouseEnter,
  handleMouseLeave,
  rowSelectStyle,
  isSearchQueryLoading,
  isPageChanging,
  isPanelFocused,
  isDisabled = false,
  userAddress,
  existsNoUsers,
  hasNotSearchedFor,
}) => (
  <div
    className="panel panel-default"
    style={isPanelFocused ? styles.focusedPanel : styles.unfocusedPanel}
    onMouseEnter={handleMouseEnter}
    onMouseLeave={handleMouseLeave}
  >
    <div className="panel-heading text-center">
      <div className="panel-title">{panelTitle}</div>
    </div>

    <div className="panel-body" style={styles.panelBody}>
      <MultipleConditionSearchComponent
        fieldAttributes={fieldAttributes}
        handleFieldChange={handleFieldChange}
        handleSubmit={handleSubmit}
        isSubmitting={isDisabled}
        columnSize={6}
      />

      <LoadingSpinner isLoading={isSearchQueryLoading} />

      {!isSearchQueryLoading && existsNoUsers ? (
        <Fragment>
          <h5>Sistemoje dar nėra užregistruotų {userAddress}.</h5>
          <h6>
            Užregistruokite bent vieną {`${userAddress.slice(0, -1)}ą`}, kad
            galėtumėte matyti jų sąrašą.
          </h6>
        </Fragment>
      ) : null}

      {!hasNotSearchedFor &&
      !isSearchQueryLoading &&
      !existsNoUsers &&
      users.length === 0 ? (
        <NoSearchResultsNotification>
          <h5>
            Pagal Jūsų įvestas paieškos frazes {userAddress} nebuvo rasta.
          </h5>
          <h6>
            Bandykite patikslinti paieškos frazes ir ieškoti dar kartą.<br />{' '}
            Rašymas didžiosiomis ar mažosiomis raidėmis paieškos rezultatų
            neįtakoja.<br />Paieškos frazėms taikoma dalinė atitiktis.
          </h6>
        </NoSearchResultsNotification>
      ) : null}

      {!isSearchQueryLoading && !existsNoUsers && users.length > 0 ? (
        <Fragment>
          <LoadingSpinner isLoading={isPageChanging} />
          {!isPageChanging ? (
            <Table condensed hover>
              <thead>
                <tr>
                  <th>Vardas</th>
                  <th>Pavardė</th>
                  <th>{thirdAttributeTitle}</th>
                  <th>Vartotojo vardas</th>
                </tr>
              </thead>
              <tbody onClick={handleTableRowClick} style={styles.tbody}>
                {users.map(user => (
                  <tr
                    id={user.id}
                    key={user.id}
                    style={selectedUser.id === user.id ? rowSelectStyle : null}
                  >
                    <td>{user.firstName}</td>
                    <td>{user.lastName}</td>
                    <td>{user[thirdAttributeName]}</td>
                    <td>{user.username}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : null}

          <PaginationComponent
            activePageNumber={pageNumber}
            hasNextPage={hasNextPage}
            handlePageSelect={handlePageSelect}
            isPageChanging={isPageChanging}
            bsSize="small"
          />
        </Fragment>
      ) : null}
    </div>
  </div>
);

export default AssignmentPanel;
