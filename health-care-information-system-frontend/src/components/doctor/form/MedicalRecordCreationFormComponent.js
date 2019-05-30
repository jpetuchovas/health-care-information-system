import { Field } from 'formik';
import React from 'react';
import {
  Button,
  Checkbox,
  Col,
  ControlLabel,
  Form,
  FormControl,
  FormGroup,
  HelpBlock,
  InputGroup,
} from 'react-bootstrap';

import RegistrationSubmitAlert from '../../common/form/alert/RegistrationSubmitAlert';
import RegistrationUnknownErrorSubmitAlert from '../../common/form/alert/RegistrationUnknownErrorSubmitAlert';
import SubmitButton from '../../common/form/button/SubmitButton';
import HorizontalTextFieldWithValidation from '../../common/form/text-input/HorizontalTextFieldWithValidation';

const styles = {
  minutesAddon: {
    paddingLeft: '7px',
  },

  submitBottom: {
    marginRight: '15px',
  },

  textarea: {
    height: '150px',
  },
};

const MedicalRecordCreationFormComponent = ({
  hasChanged,
  errors,
  handleSubmit,
  isSubmitting,
  setStatus,
  status,
  touched,
  values,
  hasGoBackButton,
  handleGoBackButtonClick,
}) => (
  <Form horizontal onSubmit={handleSubmit}>
    <h3>Naujas ligos įrašas</h3>

    <HorizontalTextFieldWithValidation
      name="personalIdentificationNumber"
      type="text"
      label="Paciento asmens kodas"
      placeholder="Paciento asmens kodas"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    <FormGroup>
      <Col smOffset={2} sm={10}>
        <Field
          name="isVisitCompensated"
          render={({ field, form: { isSubmitting } }) => (
            <Checkbox
              {...field}
              checked={values.isVisitCompensated}
              disabled={isSubmitting}
            >
              Vizitas kompensuojamas Valstybinės ligonių kasos
            </Checkbox>
          )}
        />
      </Col>
    </FormGroup>

    <FormGroup>
      <Col smOffset={2} sm={10}>
        <Field
          name="isVisitRepeated"
          render={({ field, form: { isSubmitting } }) => (
            <Checkbox
              {...field}
              checked={values.isVisitRepeated}
              disabled={isSubmitting}
            >
              Vizitas pakartotinis dėl tos pačios priežasties
            </Checkbox>
          )}
        />
      </Col>
    </FormGroup>

    <HorizontalTextFieldWithValidation
      name="diseaseCode"
      type="text"
      label="TLK-10 ligos kodas"
      placeholder="TLK-10 ligos kodas"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    <FormGroup
      controlId="visitDurationInMinutes"
      validationState={
        (touched.visitDurationInMinutes && errors.visitDurationInMinutes) ||
        (status &&
          status.serverError.visitDurationInMinutes &&
          status.serverError.value === values.visitDurationInMinutes)
          ? 'error'
          : null
      }
    >
      <Col componentClass={ControlLabel} sm={2}>
        Vizito trukmė
      </Col>
      <Col sm={3}>
        <InputGroup>
          <Field
            name="visitDurationInMinutes"
            render={({ field, form: { isSubmitting } }) => (
              <FormControl
                {...field}
                type="text"
                placeholder="Vizito trukmė"
                disabled={isSubmitting}
              />
            )}
          />
          <InputGroup.Addon style={styles.minutesAddon}>min.</InputGroup.Addon>
        </InputGroup>

        {touched.visitDurationInMinutes && errors.visitDurationInMinutes ? (
          <HelpBlock>{errors.visitDurationInMinutes}</HelpBlock>
        ) : null}

        {status &&
        status.serverError.visitDurationInMinutes &&
        status.serverError.visitDurationInMinutes ===
          values.visitDurationInMinutes ? (
          <HelpBlock>{status.serverError.visitDurationInMinutes}</HelpBlock>
        ) : null}
      </Col>
    </FormGroup>

    <FormGroup
      controlId="description"
      validationState={
        (touched.description && errors.description) ||
        (status &&
          status.serverError.description &&
          status.serverError.value === values.description)
          ? 'error'
          : null
      }
    >
      <Col componentClass={ControlLabel} sm={2}>
        Vizito aprašymas
      </Col>
      <Col sm={8}>
        <Field
          name="description"
          render={({ field, form: { isSubmitting } }) => (
            <FormControl
              {...field}
              componentClass="textarea"
              placeholder="Vizito aprašymas"
              disabled={isSubmitting}
              style={styles.textarea}
            />
          )}
        />

        {touched.description && errors.description ? (
          <HelpBlock>{errors.description}</HelpBlock>
        ) : null}

        {status &&
        status.serverError.description &&
        status.serverError.value === values.description ? (
          <HelpBlock>{status.serverError.description}</HelpBlock>
        ) : null}
      </Col>
    </FormGroup>

    {status &&
    status.serverValidationState === 'success' &&
    status.isAlertVisible &&
    !hasChanged &&
    Object.keys(errors).every(value => !value) ? (
      <RegistrationSubmitAlert
        type="success"
        text="Naujas ligos įrašas sėkmingai sukurtas."
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
          text="Sukurti"
          isSubmitting={isSubmitting}
          isSubmittingText="Kuriama..."
          style={styles.submitBottom}
        />

        {hasGoBackButton ? (
          <Button disabled={isSubmitting} onClick={handleGoBackButtonClick}>
            Grįžti atgal
          </Button>
        ) : null}
      </Col>
    </FormGroup>
  </Form>
);

export default MedicalRecordCreationFormComponent;
