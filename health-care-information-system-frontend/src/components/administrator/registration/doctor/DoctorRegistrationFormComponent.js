import PropTypes from 'prop-types';
import { Field } from 'formik';
import React from 'react';
import { Col, ControlLabel, FormControl, FormGroup } from 'react-bootstrap';

import HorizontalTextFieldWithValidation from '../../../common/form/text-input/HorizontalTextFieldWithValidation';
import RegistrationFormComponent from '../RegistrationFormComponent';

const DoctorRegistrationFormComponent = ({
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
    nameOfFirstName="doctorFirstName"
    nameOfLastName="doctorLastName"
    nameOfUsername="doctorUsername"
    nameOfPassword="doctorPassword"
    nameOfPasswordConfirmation="doctorPasswordConfirmation"
    successfulRegistrationText="Gydytojas sėkmingai užregistruotas."
  >
    <FormGroup
      controlId={values.specialization}
      validationState={
        touched.specialization && errors.specialization ? 'error' : null
      }
    >
      <Col componentClass={ControlLabel} sm={2}>
        Specializacija
      </Col>
      <Col sm={8}>
        <Field
          name="specialization"
          render={({ field, form: { isSubmitting } }) => (
            <FormControl
              {...field}
              componentClass="select"
              disabled={isSubmitting}
            >
              <option value="Akušeris ginekologas">Akušeris ginekologas</option>
              <option value="Alergologas">Alergologas</option>
              <option value="Anesteziologas">Anesteziologas</option>
              <option value="Chirurgas">Chirurgas</option>
              <option value="Dermatologas">Dermatologas</option>
              <option value="Dermatovenerologas">Dermatovenerologas</option>
              <option value="Dietologas">Dietologas</option>
              <option value="Endokrinologas">Endokrinologas</option>
              <option value="Gastroenterologas">Gastroenterologas</option>
              <option value="Hematologas">Hematologas</option>
              <option value="Kardiologas">Kardiologas</option>
              <option value="Nefrologas">Nefrologas</option>
              <option value="Neonatologas">Neonatologas</option>
              <option value="Neurochirurgas">Neurochirurgas</option>
              <option value="Neurologas">Neurologas</option>
              <option value="Odontologas">Odontologas</option>
              <option value="Oftalmologas">Oftalmologas</option>
              <option value="Onkologas">Onkologas</option>
              <option value="Ortodontas">Ortodontas</option>
              <option value="Otorinolaringologas">Otorinolaringologas</option>
              <option value="Pediatras">Pediatras</option>
              <option value="Plastikos chirurgas">Plastikos chirurgas</option>
              <option value="Psichiatras">Psichiatras</option>
              <option value="Pulmonologas">Pulmonologas</option>
              <option value="Radiologas">Radiologas</option>
              <option value="Reanimatologas">Reanimatologas</option>
              <option value="Reumatologas">Reumatologas</option>
              <option value="Šeimos gydytojas">Šeimos gydytojas</option>
              <option value="Sporto medicinos gydytojas">
                Sporto medicinos gydytojas
              </option>
              <option value="Toksikologas">Toksikologas</option>
              <option value="Urologas">Urologas</option>
              <option value="Vaikų chirurgas">Vaikų chirurgas</option>
              <option value="Vaikų endokrinologas">Vaikų endokrinologas</option>
              <option value="Vaikų hematologas">Vaikų hematologas</option>
              <option value="Vaikų ir paauglių psichiatras">
                Vaikų ir paauglių psichiatras
              </option>
              <option value="Vaikų kardiologas">Vaikų kardiologas</option>
              <option value="Vaikų nefrologas">Vaikų nefrologas</option>
              <option value="Vaikų neurologas">Vaikų neurologas</option>
              <option value="Vaikų oftalmologas">Vaikų oftalmologas</option>
              <option value="Vaikų psichiatras">Vaikų psichiatras</option>
              <option value="Vaikų pulmonologas">Vaikų pulmonologas</option>
              <option value="Vaikų reumatologas">Vaikų reumatologas</option>
              <option value="Vaikų urologas">Vaikų urologas</option>
              <option value="Veido žandikaulių chirurgas">
                Veido žandikaulių chirurgas
              </option>
              <option value="Vidaus ligų gydytojas">
                Vidaus ligų gydytojas
              </option>
              <option value="Kitas">Kitas</option>
            </FormControl>
          )}
        />
      </Col>
    </FormGroup>

    {values.specialization === 'Kitas' ? (
      <HorizontalTextFieldWithValidation
        name="otherSpecialization"
        type="text"
        placeholder="Įveskite specializaciją"
        errors={errors}
        touched={touched}
        status={status}
        values={values}
      />
    ) : null}
  </RegistrationFormComponent>
);

DoctorRegistrationFormComponent.propTypes = {
  hasChanged: PropTypes.bool.isRequired,
  errors: PropTypes.object.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  status: PropTypes.object,
  setStatus: PropTypes.func.isRequired,
  touched: PropTypes.object.isRequired,
  values: PropTypes.object.isRequired,
};

export default DoctorRegistrationFormComponent;
