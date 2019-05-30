import React from 'react';
import { Image } from 'react-bootstrap';

import faceSad from '../../../images/face-sad.svg';

const styles = {
  image: {
    height: '35px',
  },

  noSearchResultsNotification: {
    marginTop: '11.5px',
  },
};

const NoSearchResultsNotification = props => {
  return (
    <div
      id="no-search-results-notification"
      style={styles.noSearchResultsNotification}
    >
      <Image src={faceSad} style={styles.image} />
      {props.children}
    </div>
  );
};

export default NoSearchResultsNotification;
