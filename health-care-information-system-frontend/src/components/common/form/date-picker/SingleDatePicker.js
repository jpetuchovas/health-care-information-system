import React from 'react';
import {
  Button,
  Col,
  ControlLabel,
  FormGroup,
  Glyphicon,
  HelpBlock,
} from 'react-bootstrap';
import { DateRangePicker } from 'react-bootstrap-daterangepicker';

import './SingleDatePicker.css';

const styles = {
  button: {
    textTransform: 'none',
  },

  calendarGlyphicon: {
    marginRight: '10px',
  },

  menuDownGlyphicon: {
    marginLeft: '10px',
  },
};

export const SingleDatePicker = ({
  name,
  label = '',
  placeholder = '',
  errors,
  touched,
  status,
  values,
  labelColumnSize = 2,
  fieldColumnSize = 4,
  startDate,
  minDate,
  maxDate,
  setFieldValue,
}) => (
  <FormGroup
    controlId={name}
    validationState={
      (touched[name] && errors[name]) ||
      (status &&
        status.serverError[name] &&
        status.serverError.value === values[name])
        ? 'error'
        : null
    }
  >
    <Col componentClass={ControlLabel} sm={labelColumnSize}>
      {label}
    </Col>
    <Col sm={fieldColumnSize}>
      <DateRangePicker
        startDate={startDate}
        minDate={minDate}
        maxDate={maxDate}
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
        onApply={(event, picker) =>
          setFieldValue(name, picker.startDate.format('YYYY-MM-DD'))
        }
        showDropdowns
        singleDatePicker
      >
        <Button style={styles.button}>
          <Glyphicon glyph="calendar" style={styles.calendarGlyphicon} />
          {values[name] ? values[name] : placeholder}
          <Glyphicon glyph="menu-down" style={styles.menuDownGlyphicon} />
        </Button>
      </DateRangePicker>

      {touched[name] && errors[name] ? (
        <HelpBlock>{errors[name]}</HelpBlock>
      ) : null}

      {status &&
      status.serverError[name] &&
      status.serverError.value === values[name] ? (
        <HelpBlock>{status.serverError[name]}</HelpBlock>
      ) : null}
    </Col>
  </FormGroup>
);

export default SingleDatePicker;
