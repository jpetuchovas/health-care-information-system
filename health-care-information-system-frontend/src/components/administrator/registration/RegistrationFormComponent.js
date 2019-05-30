import PropTypes from 'prop-types';
import React from 'react';
import { Form } from 'react-bootstrap';

import RegistrationSubmitAlert from '../../common/form/alert/RegistrationSubmitAlert';
import RegistrationUnknownErrorSubmitAlert from '../../common/form/alert/RegistrationUnknownErrorSubmitAlert';
import RegistrationSubmitButton from '../../common/form/button/RegistrationSubmitButton';
import HorizontalTextFieldWithValidation from '../../common/form/text-input/HorizontalTextFieldWithValidation';

const RegistrationFormComponent = ({
  hasChanged,
  errors,
  handleSubmit,
  isSubmitting,
  setStatus,
  status,
  touched,
  values,
  children,
  nameOfFirstName,
  nameOfLastName,
  nameOfUsername,
  nameOfPassword,
  nameOfPasswordConfirmation,
  successfulRegistrationText,
}) => (
  <Form horizontal onSubmit={handleSubmit}>
    <HorizontalTextFieldWithValidation
      name={nameOfFirstName}
      type="text"
      label="Vardas"
      placeholder="Vardas"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    <HorizontalTextFieldWithValidation
      name={nameOfLastName}
      type="text"
      label="Pavardė"
      placeholder="Pavardė"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    {children}

    <HorizontalTextFieldWithValidation
      name={nameOfUsername}
      type="text"
      label="Vartotojo vardas"
      placeholder="Vartotojo vardas"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    <HorizontalTextFieldWithValidation
      name={nameOfPassword}
      type="password"
      label="Slaptažodis"
      placeholder="Slaptažodis"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    <HorizontalTextFieldWithValidation
      name={nameOfPasswordConfirmation}
      type="password"
      label="Pakartokite slaptažodį"
      placeholder="Pakartokite slaptažodį"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    {status &&
    status.serverValidationState === 'success' &&
    status.isAlertVisible &&
    !hasChanged &&
    Object.keys(errors).every(value => !value) ? (
      <RegistrationSubmitAlert
        type="success"
        text={successfulRegistrationText}
        handleDismiss={() => setStatus({ ...status, isAlertVisible: false })}
      />
    ) : null}

    {status &&
    status.serverValidationState === 'unknownError' &&
    status.isAlertVisible ? (
      <RegistrationUnknownErrorSubmitAlert
        handleDismiss={() => setStatus({ ...status, isAlertVisible: false })}
      />
    ) : null}

    <RegistrationSubmitButton isSubmitting={isSubmitting} />
  </Form>
);

RegistrationFormComponent.propTypes = {
  hasChanged: PropTypes.bool.isRequired,
  errors: PropTypes.object.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  status: PropTypes.object,
  setStatus: PropTypes.func.isRequired,
  touched: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
};

export default RegistrationFormComponent;
