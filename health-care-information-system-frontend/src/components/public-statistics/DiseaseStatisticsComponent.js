import { ResponsiveBar } from '@nivo/bar';
import React, { Fragment } from 'react';
import { Table } from 'react-bootstrap';

import { TRANSPARENT_BLUE_COLOR } from '../../common/constants';
import { getPercentageText } from '../../common/dataTransformation';
import LoadingSpinner from '../common/loading/LoadingSpinner';

const colorBy = () => {
  return TRANSPARENT_BLUE_COLOR;
};

const styles = {
  chart: {
    height: '400px',
  },

  table: {
    marginTop: '20px',
  },

  th: {
    width: '250px',
  },

  title: {
    marginLeft: '20px',
  },
};

const DiseaseStatisticsComponent = ({ diseases, isLoading, existsNoData }) => (
  <Fragment>
    <LoadingSpinner isLoading={isLoading} />

    {!isLoading && existsNoData ? (
      <Fragment>
        <h5>Sistemoje dar nėra ligos įrašų.</h5>
        <h6>
          Esant didesniam duomenų kiekiui, čia matysite dažniausiai
          pasitaikančių ligų<br /> procentines dalis nuo visų susirgimų.
        </h6>
      </Fragment>
    ) : null}

    {!isLoading && !existsNoData ? (
      <Fragment>
        <h4 style={styles.title}>
          Dažniausiai pasitaikančių ligų procentinės dalys nuo visų susirgimų
        </h4>

        <div style={styles.chart}>
          <ResponsiveBar
            data={diseases.map(disease => ({
              'TLK-10 ligos kodas': disease.diseaseCode,
              'Procentinė dalis nuo visų susirgimų': disease.percentage,
            }))}
            indexBy="TLK-10 ligos kodas"
            keys={['Procentinė dalis nuo visų susirgimų']}
            margin={{ top: 40, right: 0, bottom: 70, left: 10 }}
            padding={0.2}
            colorBy={colorBy}
            axisBottom={{
              legend: 'TLK-10 ligos kodas',
              legendPosition: 'center',
              legendOffset: 60,
              tickRotation: -30,
            }}
            axisLeft={null}
            enableGridY={false}
            labelTextColor="inherit:darker(1.4)"
            labelFormat={getPercentageText}
            tooltipFormat={getPercentageText}
          />
        </div>

        <Table style={styles.table} condensed hover responsive>
          <tbody>
            <tr>
              <th style={styles.th}>TLK-10 ligos kodas</th>
              {diseases.map((disease, index) => (
                <td key={index}>{disease.diseaseCode}</td>
              ))}
            </tr>

            <tr>
              <th style={styles.th}>Procentinė dalis nuo visų susirgimų</th>
              {diseases.map((disease, index) => (
                <td key={index}>{getPercentageText(disease.percentage)}</td>
              ))}
            </tr>
          </tbody>
        </Table>
      </Fragment>
    ) : null}
  </Fragment>
);

export default DiseaseStatisticsComponent;
