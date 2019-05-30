import React, { Fragment } from 'react';
import { Col, Form, FormGroup } from 'react-bootstrap';

import RegistrationSubmitAlert from '../common/form/alert/RegistrationSubmitAlert';
import RegistrationUnknownErrorSubmitAlert from '../common/form/alert/RegistrationUnknownErrorSubmitAlert';
import SubmitButton from '../common/form/button/SubmitButton';
import HorizontalTextFieldWithValidation from '../common/form/text-input/HorizontalTextFieldWithValidation';

const PasswordChangeFormComponent = ({
  hasChanged,
  errors,
  handleSubmit,
  isSubmitting,
  setStatus,
  status,
  touched,
  values,
}) => (
  <Fragment>
    <h3>Slaptažodžio keitimas</h3>

    <Form horizontal onSubmit={handleSubmit}>
      <HorizontalTextFieldWithValidation
        name="oldPassword"
        type="password"
        label="Dabartinis slaptažodis"
        placeholder="Dabartinis slaptažodis"
        errors={errors}
        touched={touched}
        status={status}
        values={values}
      />

      <HorizontalTextFieldWithValidation
        name="newPassword"
        type="password"
        label="Naujas slaptažodis"
        placeholder="Naujas slaptažodis"
        errors={errors}
        touched={touched}
        status={status}
        values={values}
      />

      <HorizontalTextFieldWithValidation
        name="newPasswordConfirmation"
        type="password"
        label="Pakartokite naują slaptažodį"
        placeholder="Pakartokite naują slaptažodį"
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
          text="Slaptažodis sėkmingai pakeistas."
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

      <FormGroup>
        <Col smOffset={2} sm={10}>
          <SubmitButton
            text="Keisti slaptažodį"
            isSubmitting={isSubmitting}
            isSubmittingText="Slaptažodis keičiamas..."
          />
        </Col>
      </FormGroup>
    </Form>
  </Fragment>
);

export default PasswordChangeFormComponent;
