import PropTypes from 'prop-types';
import React from 'react';
import { Alert } from 'react-bootstrap';

const SubmitAlert = ({ type, text, handleDismiss }) => (
  <Alert bsStyle={type} onDismiss={handleDismiss}>
    <p className="text-center">{text}</p>
  </Alert>
);

SubmitAlert.propTypes = {
  type: PropTypes.string.isRequired,
  text: PropTypes.string.isRequired,
  handleDismiss: PropTypes.func.isRequired,
};

export default SubmitAlert;
