import PropTypes from 'prop-types';
import React from 'react';
import { Col, FormGroup } from 'react-bootstrap';

import SubmitAlert from './SubmitAlert';

const RegistrationSubmitAlert = ({ type, text, handleDismiss }) => (
  <FormGroup>
    <Col smOffset={2} sm={8}>
      <SubmitAlert type={type} text={text} handleDismiss={handleDismiss} />
    </Col>
  </FormGroup>
);

RegistrationSubmitAlert.propTypes = {
  type: PropTypes.string.isRequired,
  text: PropTypes.string.isRequired,
  handleDismiss: PropTypes.func.isRequired,
};

export default RegistrationSubmitAlert;
