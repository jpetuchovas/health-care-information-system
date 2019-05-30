import PropTypes from 'prop-types';
import React from 'react';

import SubmitAlert from './SubmitAlert';

const UnknownErrorSubmitAlert = ({ handleDismiss }) => (
  <SubmitAlert
    type="danger"
    text="Įvyko klaida siunčiant užklausą į serverį. Bandykite dar kartą."
    handleDismiss={handleDismiss}
  />
);

UnknownErrorSubmitAlert.propTypes = {
  handleDismiss: PropTypes.func.isRequired,
};

export default UnknownErrorSubmitAlert;
