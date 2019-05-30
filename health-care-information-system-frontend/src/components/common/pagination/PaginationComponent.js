import PropTypes from 'prop-types';
import React from 'react';
import { Pagination } from 'react-bootstrap';

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
  },

  pagination: {
    marginTop: '-15px',
  },
};

const PaginationComponent = ({
  activePageNumber,
  hasNextPage,
  isPageChanging,
  handlePageSelect,
  marginBottom = '0',
  ...props
}) => (
  <div style={styles.container}>
    <Pagination
      prev
      next
      first={activePageNumber > 3 || (activePageNumber === 3 && hasNextPage)}
      ellipsis={hasNextPage}
      items={hasNextPage ? activePageNumber + 2 : activePageNumber}
      activePage={activePageNumber}
      maxButtons={activePageNumber === 1 ? 2 : 3}
      onSelect={handlePageSelect}
      style={
        isPageChanging
          ? { marginBottom }
          : { ...styles.pagination, marginBottom }
      }
      {...props}
    />
  </div>
);

PaginationComponent.propTypes = {
  activePageNumber: PropTypes.number.isRequired,
  hasNextPage: PropTypes.bool.isRequired,
  isPageChanging: PropTypes.bool.isRequired,
  handlePageSelect: PropTypes.func.isRequired,
  marginBottom: PropTypes.string,
};

export default PaginationComponent;
