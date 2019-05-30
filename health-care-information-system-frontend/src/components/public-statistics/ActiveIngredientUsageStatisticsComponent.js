import { ResponsiveBar } from '@nivo/bar';
import React, { Fragment } from 'react';
import { Table } from 'react-bootstrap';

import { TRANSPARENT_BLUE_COLOR } from '../../common/constants';
import { addThousandsSeparators } from '../../common/dataTransformation';
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

  title: {
    marginLeft: '20px',
  },
};

const ActiveIngredientUsageStatisticsComponent = ({
  activeIngredients,
  isLoading,
  existsNoData,
}) => (
  <Fragment>
    <LoadingSpinner isLoading={isLoading} />

    {!isLoading && existsNoData ? (
      <Fragment>
        <h5>Sistemoje dar nėra panaudotų receptų.</h5>
        <h6>
          Esant didesniam duomenų kiekiui, čia matysite dažniausiai naudojamų
          veikliųjų medžiagų<br /> panaudojimo faktų skaičių.
        </h6>
      </Fragment>
    ) : null}

    {!isLoading && !existsNoData ? (
      <Fragment>
        <h4 style={styles.title}>
          Dažniausiai naudojamų veikliųjų medžiagų panaudojimo faktų skaičius
        </h4>

        <div style={styles.chart}>
          <ResponsiveBar
            data={activeIngredients.map(activeIngredient => ({
              'Veiklioji medžiaga': activeIngredient.activeIngredient,
              'Panaudojimo faktų skaičius': activeIngredient.usageCount,
            }))}
            indexBy="Veiklioji medžiaga"
            keys={['Panaudojimo faktų skaičius']}
            margin={{ top: 40, right: 0, bottom: 130, left: 50 }}
            padding={0.2}
            colorBy={colorBy}
            axisBottom={{
              legend: 'Veiklioji medžiaga',
              legendPosition: 'center',
              legendOffset: 120,
              tickRotation: -30,
            }}
            axisLeft={null}
            enableGridY={false}
            labelTextColor="inherit:darker(1.4)"
            labelFormat={addThousandsSeparators}
            tooltipFormat={addThousandsSeparators}
          />
        </div>

        <Table style={styles.table} condensed hover responsive>
          <tbody>
            <tr>
              <th>Veiklioji medžiaga</th>
              {activeIngredients.map((activeIngredient, index) => (
                <td key={index}>{activeIngredient.activeIngredient}</td>
              ))}
            </tr>

            <tr>
              <th>Panaudojimo faktų skaičius</th>
              {activeIngredients.map((activeIngredient, index) => (
                <td key={index}>
                  {addThousandsSeparators(activeIngredient.usageCount)}
                </td>
              ))}
            </tr>
          </tbody>
        </Table>
      </Fragment>
    ) : null}
  </Fragment>
);

export default ActiveIngredientUsageStatisticsComponent;
