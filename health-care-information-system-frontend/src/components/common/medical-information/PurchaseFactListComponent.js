import React, { Fragment } from 'react';
import { ListGroup, ListGroupItem } from 'react-bootstrap';

import LoadingSpinner from '../loading/LoadingSpinner';
import PaginationComponent from '../pagination/PaginationComponent';

const styles = {
  purchaseFactListGroup: {
    marginBottom: '23px',
    marginLeft: 0,
  },

  purchaseFactListItem: {
    padding: '5px 10px',
  },
};

const PurchaseFactListComponent = ({
  purchaseFacts,
  pageNumber,
  hasNextPage,
  isLoading,
  isPageChanging,
  handlePageSelect,
}) => (
  <Fragment>
    <LoadingSpinner isLoading={isLoading} />

    {!isLoading ? (
      <Fragment>
        <h6>Recepto panaudojimo datos</h6>
        <LoadingSpinner isLoading={isPageChanging} />

        {!isPageChanging ? (
          <ListGroup bsClass="list-inline" style={styles.purchaseFactListGroup}>
            {purchaseFacts.map((purchaseFact, index) => (
              <ListGroupItem key={index} style={styles.purchaseFactListItem}>
                {purchaseFact}
              </ListGroupItem>
            ))}
          </ListGroup>
        ) : null}

        <PaginationComponent
          activePageNumber={pageNumber}
          hasNextPage={hasNextPage}
          isPageChanging={isPageChanging}
          handlePageSelect={handlePageSelect}
          bsSize="small"
        />
      </Fragment>
    ) : null}
  </Fragment>
);

export default PurchaseFactListComponent;
