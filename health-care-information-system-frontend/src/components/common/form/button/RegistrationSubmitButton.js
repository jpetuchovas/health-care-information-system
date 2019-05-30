import PropTypes from 'prop-types';
import React from 'react';
import { Col, FormGroup } from 'react-bootstrap';

import SubmitButton from './SubmitButton';

const RegistrationSubmitButton = ({ isSubmitting }) => (
  <FormGroup>
    <Col smOffset={2} sm={10}>
      <SubmitButton
        text="Registruoti"
        isSubmitting={isSubmitting}
        isSubmittingText="Registruojama..."
      />
    </Col>
  </FormGroup>
);

RegistrationSubmitButton.propTypes = {
  isSubmitting: PropTypes.bool.isRequired,
};

export default RegistrationSubmitButton;
