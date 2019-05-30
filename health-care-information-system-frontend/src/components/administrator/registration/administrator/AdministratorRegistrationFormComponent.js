import PropTypes from 'prop-types';
import React from 'react';

import RegistrationFormComponent from '../RegistrationFormComponent';

const AdministratorRegistrationFormComponent = ({
  hasChanged,
  errors,
  handleSubmit,
  isSubmitting,
  setStatus,
  status,
  touched,
  values,
}) => (
  <RegistrationFormComponent
    hasChanged={hasChanged}
    errors={errors}
    handleSubmit={handleSubmit}
    isSubmitting={isSubmitting}
    setStatus={setStatus}
    status={status}
    touched={touched}
    values={values}
    nameOfFirstName="administratorFirstName"
    nameOfLastName="administratorLastName"
    nameOfUsername="administratorUsername"
    nameOfPassword="administratorPassword"
    nameOfPasswordConfirmation="administratorPasswordConfirmation"
    successfulRegistrationText="Administratorius sėkmingai užregistruotas."
  />
);

AdministratorRegistrationFormComponent.propTypes = {
  hasChanged: PropTypes.bool.isRequired,
  errors: PropTypes.object.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  status: PropTypes.object,
  setStatus: PropTypes.func.isRequired,
  touched: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
};

export default AdministratorRegistrationFormComponent;
