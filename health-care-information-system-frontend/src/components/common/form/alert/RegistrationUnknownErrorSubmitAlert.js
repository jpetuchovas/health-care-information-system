import PropTypes from 'prop-types';
import React from 'react';
import { Col, FormGroup } from 'react-bootstrap';

import UnknownErrorSubmitAlert from './UnknownErrorSubmitAlert';

const RegistrationUnknownErrorSubmitAlert = ({ handleDismiss }) => (
  <FormGroup>
    <Col smOffset={2} sm={8}>
      <UnknownErrorSubmitAlert handleDismiss={handleDismiss} />
    </Col>
  </FormGroup>
);

RegistrationUnknownErrorSubmitAlert.propTypes = {
  handleDismiss: PropTypes.func.isRequired,
};

export default RegistrationUnknownErrorSubmitAlert;
