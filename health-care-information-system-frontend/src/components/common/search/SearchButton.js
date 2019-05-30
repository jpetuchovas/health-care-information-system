import PropTypes from 'prop-types';
import React from 'react';
import { Button, Glyphicon } from 'react-bootstrap';

const styles = {
  button: {
    backgroundColor: '#e5e5e5',
  },

  glyphicon: {
    marginRight: '5px',
  },
};

const SearchButton = ({ handleClick, isDisabled = false, style = {} }) => (
  <Button
    type="button"
    onClick={handleClick}
    style={{ ...styles.button, ...style }}
    disabled={isDisabled}
  >
    <Glyphicon glyph="search" style={styles.glyphicon} />
    Ieškoti
  </Button>
);

SearchButton.propTypes = {
  handleClick: PropTypes.func.isRequired,
  isDisabled: PropTypes.bool,
};

export default SearchButton;
