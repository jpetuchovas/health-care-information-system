import { Field } from 'formik';
import PropTypes from 'prop-types';
import React from 'react';
import {
  Col,
  ControlLabel,
  FormControl,
  FormGroup,
  HelpBlock,
} from 'react-bootstrap';

const HorizontalTextFieldWithValidation = ({
  name,
  type = 'text',
  label = '',
  placeholder = '',
  errors,
  touched,
  status,
  values,
  labelColumnSize = 2,
  fieldColumnSize = 8,
}) => (
  <FormGroup
    controlId={name}
    validationState={
      (touched[name] && errors[name]) ||
      (status &&
        status.serverError[name] &&
        status.serverError.value.toLowerCase() === values[name].toLowerCase())
        ? 'error'
        : null
    }
  >
    <Col componentClass={ControlLabel} sm={labelColumnSize}>
      {label}
    </Col>
    <Col sm={fieldColumnSize}>
      <Field
        name={name}
        render={({ field, form: { isSubmitting } }) => (
          <FormControl
            {...field}
            type={type}
            placeholder={placeholder}
            disabled={isSubmitting}
          />
        )}
      />

      {touched[name] && errors[name] ? (
        <HelpBlock>{errors[name]}</HelpBlock>
      ) : null}

      {status &&
      status.serverError[name] &&
      status.serverError.value.toLowerCase() === values[name].toLowerCase() ? (
        <HelpBlock>{status.serverError[name]}</HelpBlock>
      ) : null}
    </Col>
  </FormGroup>
);

HorizontalTextFieldWithValidation.propTypes = {
  name: PropTypes.string.isRequired,
  type: PropTypes.oneOf(['text', 'password']),
  label: PropTypes.string,
  placeholder: PropTypes.string,
  errors: PropTypes.object.isRequired,
  touched: PropTypes.object.isRequired,
  status: PropTypes.object,
  labelColumnSize: PropTypes.number,
  fieldColumnSize: PropTypes.number,
};

export default HorizontalTextFieldWithValidation;
