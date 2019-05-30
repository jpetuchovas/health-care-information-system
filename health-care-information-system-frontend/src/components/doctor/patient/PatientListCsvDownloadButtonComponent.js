import React from 'react';
import { Button, Glyphicon } from 'react-bootstrap';
import { RingLoader } from 'react-spinners';

const styles = {
  button: {
    margin: '5px 0 10px',
    width: '275px',
  },

  icon: {
    marginRight: '7px',
  },

  spinner: {
    bottom: '1px',
    display: 'inline-block',
    marginLeft: '-8px',
    position: 'relative',
    top: '1px',
  },
};

const PatientListCsvDownloadButtonComponent = ({
  isDownloading,
  handleDownloadButtonClick,
}) => (
  <Button
    bsStyle="primary"
    className="pull-right download-button"
    style={styles.button}
    disabled={isDownloading}
    onClick={handleDownloadButtonClick}
  >
    {isDownloading ? (
      <div style={{ ...styles.icon, ...styles.spinner }}>
        <RingLoader size={13} margin="0px" color="white" loading={true} />
      </div>
    ) : (
      <Glyphicon glyph="save" style={styles.icon} />
    )}

    {isDownloading
      ? 'Pacientų sąrašas siunčiamas...'
      : 'Parsisiųsti visų pacientų sąrašą'}
  </Button>
);

export default PatientListCsvDownloadButtonComponent;
