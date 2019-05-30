import moment from 'moment';
import React, { Fragment } from 'react';
import { Button, Glyphicon, Table } from 'react-bootstrap';
import { DateRangePicker } from 'react-bootstrap-daterangepicker';

import { MINIMUM_VISIT_STATISTICS_DATE } from '../../../common/constants';
import AdministratorContactsComponent from '../../common/contacts/AdministratorContactsComponent';
import LoadingSpinner from '../../common/loading/LoadingSpinner';
import PaginationComponent from '../../common/pagination/PaginationComponent';
import { convertMinutesToTextWithHours } from '../../../common/dataTransformation';

const styles = {
  button: {
    display: 'flex',
    justifyContent: 'space-between',
    textTransform: 'none',
    width: '305px',
  },

  calendarGlyphicon: {
    lineHeight: 1.5,
    marginRight: '10px',
  },

  mainContent: {
    marginTop: '23px',
  },

  menuDownGlyphicon: {
    lineHeight: 1.5,
    marginLeft: 'auto',
  },

  noVisitsQuestion: {
    marginBottom: '5px',
  },

  title: {
    marginBottom: '23px',
  },
};

const VisitStatisticsComponent = ({
  startDate,
  endDate,
  visitAggregateCount,
  visitDurationInMinutesAggregateSum,
  visits,
  pageNumber,
  hasNextPage,
  isLoading,
  isPageChanging,
  hasNoVisits,
  handleApply,
  handlePageSelect,
}) => (
  <Fragment>
    <h3 style={styles.title}>Darbo dienų statistika</h3>

    <DateRangePicker
      applyClass="btn-primary"
      linkedCalendars={false}
      startDate={moment()
        .subtract(29, 'days')
        .format('YYYY-MM-DD')}
      endDate={moment()
        .subtract(29, 'days')
        .format('YYYY-MM-DD')}
      minDate={MINIMUM_VISIT_STATISTICS_DATE}
      maxDate={moment().format('YYYY-MM-DD')}
      locale={{
        format: 'YYYY-MM-DD',
        separator: ' - ',
        applyLabel: 'Pasirinkti',
        cancelLabel: 'Atšaukti',
        fromLabel: 'Nuo',
        toLabel: 'Iki',
        customRangeLabel: 'Pasirinkti laikotarpį',
        weekLabel: 'Nr',
        daysOfWeek: ['Pr', 'A', 'T', 'K', 'Pn', 'Š', 'S'],
        monthNames: [
          'Sausis',
          'Vasaris',
          'Kovas',
          'Balandis',
          'Gegužė',
          'Birželis',
          'Liepa',
          'Rugpjūtis',
          'Rugsėjis',
          'Spalis',
          'Lapkritis',
          'Gruodis',
        ],
        firstDay: 1,
      }}
      opens="center"
      onApply={handleApply}
      showDropdowns
      autoApply
    >
      <Button style={styles.button}>
        <Glyphicon glyph="calendar" style={styles.calendarGlyphicon} />
        {startDate && endDate
          ? `Nuo ${startDate} iki ${endDate}`
          : 'Pasirinkite laikotarpį'}
        <Glyphicon glyph="menu-down" style={styles.menuDownGlyphicon} />
      </Button>
    </DateRangePicker>

    <div style={styles.mainContent}>
      <LoadingSpinner isLoading={isLoading} />

      {!isLoading && hasNoVisits ? (
        <div id="no-visits-notification">
          <h5>Per nurodytą laikotarpį nebuvo priimta nė vieno paciento.</h5>
          <h6 style={styles.noVisitsQuestion}>
            Nurodyto laikotarpio darbo dienų statistika neturėtų būti tuščia?
          </h6>
          <AdministratorContactsComponent />
        </div>
      ) : null}

      {!isLoading && !hasNoVisits && visits.length > 0 ? (
        <Fragment>
          <h6>
            <strong>Priimtų pacientų skaičius:</strong> {visitAggregateCount}
          </h6>
          <h6>
            <strong>Vizitų trukmė:</strong>{' '}
            {convertMinutesToTextWithHours(visitDurationInMinutesAggregateSum)}
          </h6>

          <LoadingSpinner isLoading={isPageChanging} />

          {!isPageChanging ? (
            <Table condensed hover>
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Priimtų pacientų skaičius</th>
                  <th>Vizitų trukmė</th>
                </tr>
              </thead>

              <tbody>
                {visits.map((visit, index) => (
                  <tr key={index}>
                    <td>{visit.date}</td>
                    <td>{visit.visitCount}</td>
                    <td>
                      {convertMinutesToTextWithHours(
                        visit.visitDurationInMinutesSum
                      )}
                    </td>
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
    </div>
  </Fragment>
);

export default VisitStatisticsComponent;
