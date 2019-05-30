import PropTypes from 'prop-types';
import React from 'react';
import { Glyphicon } from 'react-bootstrap';

const styles = {
  span: {
    cursor: 'pointer',
  },

  glyphicon: {
    marginRight: '5px',
  },
};

const CloseButton = ({ handleClose }) => {
  return (
    <span style={styles.span} onClick={handleClose}>
      <Glyphicon glyph="remove" style={styles.glyphicon} />
      Uždaryti
    </span>
  );
};

CloseButton.propTypes = {
  handleClose: PropTypes.func.isRequired,
};

export default CloseButton;
