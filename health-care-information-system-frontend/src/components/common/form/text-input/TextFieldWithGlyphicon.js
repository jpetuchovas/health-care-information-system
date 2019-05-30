import { Field } from 'formik';
import PropTypes from 'prop-types';
import React from 'react';
import {
  Col,
  FormControl,
  FormGroup,
  Glyphicon,
  HelpBlock,
  InputGroup,
} from 'react-bootstrap';

const TextFieldWithGlyphicon = ({
  name,
  type = 'text',
  glyph,
  placeholder = '',
  status,
  touched,
  errors,
}) => (
  <FormGroup
    controlId={name}
    validationState={
      (touched[name] && errors[name]) ||
      (status && status.serverValidationState === 'knownError')
        ? 'error'
        : null
    }
  >
    <InputGroup>
      <InputGroup.Addon>
        <Glyphicon glyph={glyph} />
      </InputGroup.Addon>
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
    </InputGroup>

    {touched[name] && errors[name] ? (
      <Col smOffset={1}>
        <HelpBlock>{errors[name]}</HelpBlock>
      </Col>
    ) : null}
  </FormGroup>
);

TextFieldWithGlyphicon.propTypes = {
  name: PropTypes.string.isRequired,
  type: PropTypes.oneOf(['text', 'password']),
  glyph: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  status: PropTypes.object,
  errors: PropTypes.object.isRequired,
  touched: PropTypes.object.isRequired,
};

export default TextFieldWithGlyphicon;
