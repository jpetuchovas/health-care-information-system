import PropTypes from 'prop-types';
import React from 'react';
import { Button } from 'react-bootstrap';

const SubmitButton = ({ text, isSubmitting, isSubmittingText, ...props }) => (
  <Button bsStyle="primary" type="submit" disabled={isSubmitting} {...props}>
    {isSubmitting ? isSubmittingText : text}
  </Button>
);

SubmitButton.propTypes = {
  text: PropTypes.string.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  isSubmittingText: PropTypes.string.isRequired,
};

export default SubmitButton;
