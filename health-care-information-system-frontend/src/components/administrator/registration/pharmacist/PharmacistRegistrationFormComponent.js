import PropTypes from 'prop-types';
import { Field } from 'formik';
import React from 'react';
import {
  Col,
  ControlLabel,
  DropdownButton,
  FormControl,
  FormGroup,
  HelpBlock,
  InputGroup,
  MenuItem,
} from 'react-bootstrap';

import RegistrationFormComponent from '../RegistrationFormComponent';

const styles = {
  workplaceDropDownButton: {
    marginRight: '10px',
    textTransform: 'none',
  },
};

const PharmacistRegistrationFormComponent = ({
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
    nameOfFirstName="pharmacistFirstName"
    nameOfLastName="pharmacistLastName"
    nameOfUsername="pharmacistUsername"
    nameOfPassword="pharmacistPassword"
    nameOfPasswordConfirmation="pharmacistPasswordConfirmation"
    successfulRegistrationText="Vaistininkas sėkmingai užregistruotas."
  >
    <FormGroup
      controlId="workplace"
      validationState={
        (touched.workplace && errors.workplace) ||
        (status &&
          status.serverError.workplace &&
          status.serverError.value === values.workplace)
          ? 'error'
          : null
      }
    >
      <Col componentClass={ControlLabel} sm={2}>
        Darbovietės pavadinimas
      </Col>
      <Col sm={8}>
        <InputGroup>
          <DropdownButton
            componentClass={InputGroup.Button}
            id="input-dropdown-addon"
            title={values.workplaceType}
            style={styles.workplaceDropDownButton}
          >
            <MenuItem
              key="1"
              onSelect={() => setFieldValue('workplaceType', 'VšĮ')}
            >
              VšĮ
            </MenuItem>
            <MenuItem
              key="2"
              onSelect={() => setFieldValue('workplaceType', 'UAB')}
            >
              UAB
            </MenuItem>
            <MenuItem
              key="3"
              onSelect={() => setFieldValue('workplaceType', 'AB')}
            >
              AB
            </MenuItem>
            <MenuItem
              key="4"
              onSelect={() => setFieldValue('workplaceType', 'MB')}
            >
              MB
            </MenuItem>
          </DropdownButton>
          <Field
            name="workplace"
            render={({ field, form: { isSubmitting } }) => (
              <FormControl
                {...field}
                type="text"
                placeholder="Darbovietės pavadinimas"
                disabled={isSubmitting}
              />
            )}
          />
        </InputGroup>

        {touched.workplace && errors.workplace ? (
          <HelpBlock>{errors.workplace}</HelpBlock>
        ) : null}

        {status &&
        status.serverError.workplace &&
        status.serverError.value === values.workplace ? (
          <HelpBlock>{status.serverError.workplace}</HelpBlock>
        ) : null}
      </Col>
    </FormGroup>
  </RegistrationFormComponent>
);

PharmacistRegistrationFormComponent.propTypes = {
  hasChanged: PropTypes.bool.isRequired,
  errors: PropTypes.object.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  status: PropTypes.object,
  setStatus: PropTypes.func.isRequired,
  touched: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
};

export default PharmacistRegistrationFormComponent;
