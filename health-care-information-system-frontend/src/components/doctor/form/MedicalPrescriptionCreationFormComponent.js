import { Field } from 'formik';
import moment from 'moment';
import React from 'react';
import {
  Button,
  Checkbox,
  Col,
  ControlLabel,
  DropdownButton,
  Form,
  FormControl,
  FormGroup,
  HelpBlock,
  InputGroup,
  MenuItem,
} from 'react-bootstrap';

import RegistrationSubmitAlert from '../../common/form/alert/RegistrationSubmitAlert';
import RegistrationUnknownErrorSubmitAlert from '../../common/form/alert/RegistrationUnknownErrorSubmitAlert';
import SubmitButton from '../../common/form/button/SubmitButton';
import SingleDatePicker from '../../common/form/date-picker/SingleDatePicker';
import HorizontalTextFieldWithValidation from '../../common/form/text-input/HorizontalTextFieldWithValidation';

const styles = {
  activeIngredientQuantityDropDownButton: {
    marginLeft: '2px',
    textTransform: 'none',
  },

  submitBottom: {
    marginRight: '15px',
  },

  textarea: {
    height: '150px',
  },
};

const MedicalPrescriptionCreationFormComponent = ({
  hasChanged,
  errors,
  handleSubmit,
  isSubmitting,
  setStatus,
  status,
  touched,
  values,
  setFieldValue,
  hasGoBackButton,
  handleGoBackButtonClick,
}) => (
  <Form horizontal onSubmit={handleSubmit}>
    <h3>Naujas receptas</h3>

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
          name="hasUnlimitedValidity"
          render={({ field, form: { isSubmitting } }) => (
            <Checkbox
              {...field}
              checked={values.hasUnlimitedValidity}
              disabled={isSubmitting}
            >
              Neterminuotas receptas
            </Checkbox>
          )}
        />
      </Col>
    </FormGroup>

    {!values.hasUnlimitedValidity ? (
      <SingleDatePicker
        name="validityEndDate"
        label="Galiojimo pabaigos data"
        placeholder="MMMM-mm-dd"
        errors={errors}
        touched={touched}
        status={status}
        values={values}
        startDate={moment()
          .add(29, 'days')
          .format('YYYY-MM-DD')}
        minDate={moment()
          .add(1, 'days')
          .format('YYYY-MM-DD')}
        setFieldValue={setFieldValue}
      />
    ) : null}

    <HorizontalTextFieldWithValidation
      name="activeIngredient"
      type="text"
      label="Vaisto veiklioji mežiaga"
      placeholder="Vaisto veiklioji mežiaga"
      errors={errors}
      touched={touched}
      status={status}
      values={values}
    />

    <FormGroup
      controlId="activeIngredientQuantity"
      validationState={
        (touched.activeIngredientQuantity && errors.activeIngredientQuantity) ||
        (status &&
          status.serverError.activeIngredientQuantity &&
          status.serverError.value === values.activeIngredientQuantity)
          ? 'error'
          : null
      }
    >
      <Col componentClass={ControlLabel} sm={2}>
        Veikliosios medžiagos kiekis
      </Col>
      <Col sm={4}>
        <InputGroup>
          <Field
            name="activeIngredientQuantity"
            render={({ field, form: { isSubmitting } }) => (
              <FormControl
                {...field}
                type="text"
                placeholder="Veikliosios medžiagos kiekis"
                disabled={isSubmitting}
              />
            )}
          />

          <DropdownButton
            componentClass={InputGroup.Button}
            id="input-dropdown-addon"
            title={values.activeIngredientMeasurementUnit}
            style={styles.activeIngredientQuantityDropDownButton}
          >
            <MenuItem
              key="1"
              onSelect={() =>
                setFieldValue('activeIngredientMeasurementUnit', 'mg')
              }
            >
              mg
            </MenuItem>
            <MenuItem
              key="2"
              onSelect={() =>
                setFieldValue('activeIngredientMeasurementUnit', 'mcg')
              }
            >
              mcg
            </MenuItem>
            <MenuItem
              key="3"
              onSelect={() =>
                setFieldValue('activeIngredientMeasurementUnit', 'IU')
              }
            >
              IU
            </MenuItem>
          </DropdownButton>
        </InputGroup>

        {touched.activeIngredientQuantity && errors.activeIngredientQuantity ? (
          <HelpBlock>{errors.activeIngredientQuantity}</HelpBlock>
        ) : null}

        {status &&
        status.serverError.activeIngredientQuantity &&
        status.serverError.value === values.activeIngredientQuantity ? (
          <HelpBlock>{status.serverError.activeIngredientQuantity}</HelpBlock>
        ) : null}
      </Col>
    </FormGroup>

    <FormGroup
      controlId="usageDescription"
      validationState={
        (touched.usageDescription && errors.usageDescription) ||
        (status &&
          status.serverError.usageDescription &&
          status.serverError.value === values.usageDescription)
          ? 'error'
          : null
      }
    >
      <Col componentClass={ControlLabel} sm={2}>
        Vartojimo aprašymas
      </Col>
      <Col sm={8}>
        <Field
          name="usageDescription"
          render={({ field, form: { isSubmitting } }) => (
            <FormControl
              {...field}
              componentClass="textarea"
              placeholder="Vartojimo aprašymas"
              disabled={isSubmitting}
              style={styles.textarea}
            />
          )}
        />

        {touched.usageDescription && errors.usageDescription ? (
          <HelpBlock>{errors.usageDescription}</HelpBlock>
        ) : null}

        {status &&
        status.serverError.usageDescription &&
        status.serverError.value === values.usageDescription ? (
          <HelpBlock>{status.serverError.usageDescription}</HelpBlock>
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
        text="Naujas receptas sėkmingai sukurtas."
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

export default MedicalPrescriptionCreationFormComponent;
