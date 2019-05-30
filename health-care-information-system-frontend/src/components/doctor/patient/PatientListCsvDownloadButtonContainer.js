import axios from 'axios';
import fileDownload from 'js-file-download';
import { state as globalState } from 'lape';
import React, { Component } from 'react';

import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import PatientListCsvDownloadButtonComponent from './PatientListCsvDownloadButtonComponent';

const CSV_FILE_NAME = 'pacientai.csv';

class PatientListCsvDownloadButtonContainer extends Component {
  state = {
    isDownloading: false,
  };

  handleDownloadButtonClick = () => {
    this.setState({ isDownloading: true });

    axios
      .get(getRequestUrl(`/api/doctors/${globalState.userId}/patients/csv`), {
        ...getAuthorizationConfig(),
        responseType: 'blob',
      })
      .then(response => {
        fileDownload(response.data, CSV_FILE_NAME);

        this.setState({ isDownloading: false });
      })
      .catch(() => {
        this.setState({ isDownloading: false });
      });
  };

  render() {
    return (
      <PatientListCsvDownloadButtonComponent
        isDownloading={this.state.isDownloading}
        handleDownloadButtonClick={this.handleDownloadButtonClick}
      />
    );
  }
}

export default PatientListCsvDownloadButtonContainer;
