import PropTypes from 'prop-types';
import React from 'react';
import { Col, Row } from 'react-bootstrap';

import SearchButton from './SearchButton';
import SearchField from './SearchField';

const styles = {
  searchButton: {
    margin: '5px 0 10px',
  },
};

const MultipleConditionSearchComponent = ({
  fieldAttributes,
  handleFieldChange,
  handleSubmit,
  isDisabled = false,
  columnSize = 3,
  ...props
}) => (
  <div>
    <Row>
      <Col sm={columnSize}>
        <SearchField
          attributes={fieldAttributes[0]}
          handleChange={handleFieldChange(fieldAttributes[0].name)}
          handleSubmit={handleSubmit}
          isDisabled={isDisabled}
        />
      </Col>

      <Col sm={columnSize}>
        <SearchField
          attributes={fieldAttributes[1]}
          handleChange={handleFieldChange(fieldAttributes[1].name)}
          handleSubmit={handleSubmit}
          isDisabled={isDisabled}
        />
      </Col>
    </Row>

    <Row>
      <Col sm={columnSize}>
        <SearchField
          attributes={fieldAttributes[2]}
          handleChange={handleFieldChange(fieldAttributes[2].name)}
          handleSubmit={handleSubmit}
          isDisabled={isDisabled}
        />
      </Col>

      <Col sm={columnSize}>
        <SearchField
          attributes={fieldAttributes[3]}
          handleChange={handleFieldChange(fieldAttributes[3].name)}
          handleSubmit={handleSubmit}
          isDisabled={isDisabled}
        />
      </Col>
    </Row>

    <Row>
      <Col sm={columnSize}>
        <SearchButton
          handleClick={handleSubmit}
          isDisabled={isDisabled}
          style={styles.searchButton}
        />
      </Col>
      {props.children}
    </Row>
  </div>
);

MultipleConditionSearchComponent.propTypes = {
  fieldAttributes: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      placeholder: PropTypes.string.isRequired,
      value: PropTypes.string.isRequired,
    })
  ).isRequired,
  handleFieldChange: PropTypes.func.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isDisabled: PropTypes.bool,
  columnSize: PropTypes.number,
};

export default MultipleConditionSearchComponent;
