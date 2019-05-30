import PropTypes from 'prop-types';
import React from 'react';
import { ControlLabel, FormControl, FormGroup } from 'react-bootstrap';

const styles = {
  formGroup: {
    marginBottom: '10px',
  },

  controlLabel: {
    marginBottom: 0,
  },
};

const SearchField = ({
  attributes,
  handleChange,
  handleSubmit,
  isDisabled = false,
}) => (
  <FormGroup
    controlId={attributes.name}
    bsSize="small"
    style={styles.formGroup}
  >
    <ControlLabel style={styles.controlLabel}>{attributes.label}</ControlLabel>
    <FormControl
      type="text"
      placeholder={attributes.placeholder}
      value={attributes.value}
      onChange={handleChange}
      onKeyPress={event => {
        if (event.key === 'Enter') {
          handleSubmit(event);
        }
      }}
      disabled={isDisabled}
    />
  </FormGroup>
);

SearchField.propTypes = {
  attributes: PropTypes.shape({
    name: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    placeholder: PropTypes.string.isRequired,
    value: PropTypes.string.isRequired,
  }),
  handleChange: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isDisabled: PropTypes.bool,
};

export default SearchField;
