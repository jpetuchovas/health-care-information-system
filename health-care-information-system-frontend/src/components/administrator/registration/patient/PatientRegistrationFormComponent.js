import moment from 'moment';
import PropTypes from 'prop-types';
import React from 'react';

import { MINIMUM_DATE } from '../../../../common/constants';
import SingleDatePicker from '../../../common/form/date-picker/SingleDatePicker';
import HorizontalTextFieldWithValidation from '../../../common/form/text-input/HorizontalTextFieldWithValidation';
import RegistrationFormComponent from '../RegistrationFormComponent';

const PatientRegistrationFormComponent = ({
  hasChanged,
  errors,
  handleSubmit,
  isSubmitting,
  setStatus,
  status,
  touched,
  values,
  setFieldValue,
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
    nameOfFirstName="patientFirstName"
    nameOfLastName="patientLastName"
    nameOfUsername="patientUsername"
    nameOfPassword="patientPassword"
    nameOfPasswordConfirmation="patientPasswordConfirmation"
    successfulRegistrationText="Pacientas sėkmingai užregistruotas."
  >
    <SingleDatePicker
      name="birthDate"
      label="Gimimo data"
      placeholder="MMMM-mm-dd"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
      startDate={moment()
        .startOf('year')
        .format('YYYY-MM-DD')}
      minDate={MINIMUM_DATE}
      maxDate={moment().format('YYYY-MM-DD')}
      setFieldValue={setFieldValue}
    />

    <HorizontalTextFieldWithValidation
      name="personalIdentificationNumber"
      type="text"
      label="Asmens kodas"
      placeholder="Asmens kodas"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />
  </RegistrationFormComponent>
);

PatientRegistrationFormComponent.propTypes = {
  hasChanged: PropTypes.bool.isRequired,
  errors: PropTypes.object.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  status: PropTypes.object,
  setStatus: PropTypes.func.isRequired,
  touched: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
};

export default PatientRegistrationFormComponent;
