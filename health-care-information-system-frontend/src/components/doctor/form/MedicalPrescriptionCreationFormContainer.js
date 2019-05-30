import axios from 'axios';
import moment from 'moment';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import Yup from 'yup';

import {
  ACTIVE_INGREDIENT_LENGTH_MAX,
  ACTIVE_INGREDIENT_QUANTITY_INTEGER_DIGITS_MAX,
  ACTIVE_INGREDIENT_QUANTITY_MAX_THRESHOLD,
  ADMINISTRATOR_EMAIL,
  ADMINISTRATOR_PHONE_NUMBER,
  DESCRIPTION_LENGTH_MAX,
  PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
} from '../../../common/constants';
import { addThousandsSeparators } from '../../../common/dataTransformation';
import Formik from '../../common/form/FormikWithHasChanged';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../../common/requestUtils';
import {
  POSITIVE_DECIMAL_NUMBERS_PATTERN,
  ACTIVE_INGREDIENT_PATTERN,
  DATE_PATTERN,
  PERSONAL_IDENTIFICATION_NUMBER_PATTERN,
  PERSONAL_IDENTIFICATION_NUMBER_START_PATTERN,
} from '../../../common/validation';
import MedicalPrescriptionCreationFormComponent from './MedicalPrescriptionCreationFormComponent';

const validationSchema = Yup.object().shape({
  personalIdentificationNumber: Yup.string()
    .required('Įveskite paciento asmens kodą.')
    .matches(
      PERSONAL_IDENTIFICATION_NUMBER_PATTERN,
      'Asmens kodas turi būti sudarytas tik iš skaitmenų.'
    )
    .matches(
      PERSONAL_IDENTIFICATION_NUMBER_START_PATTERN,
      'Asmens kodas turi prasidėti skaitmeniu 3, 4, 5 arba 6.'
    )
    .length(
      PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
      `Asmens kodas turi būti sudarytas iš ${PERSONAL_IDENTIFICATION_NUMBER_LENGTH} skaitmenų.`
    ),

  hasUnlimitedValidity: Yup.boolean(),

  validityEndDate: Yup.string().when(
    'hasUnlimitedValidity',
    (hasUnlimitedValidity, schema) =>
      !hasUnlimitedValidity
        ? schema
            .test(
              'isNonPastDate',
              'Recepto galiojimo pabaigos data negali būti praeityje arba sutapti su šios dienos.',
              value =>
                value == null ||
                !DATE_PATTERN.test(value) ||
                !moment(value, 'YYYY-MM-DD', true).isValid() ||
                moment(value, 'YYYY-MM-DD', true).isAfter(
                  moment().format('YYYY-MM-DD')
                )
            )
            .test(
              'isValidDate',
              'Įveskite egzistuojančią recepto galiojimo pabaigos datą formatu MMMM-mm-dd.',
              value =>
                value == null ||
                !DATE_PATTERN.test(value) ||
                moment(value, 'YYYY-MM-DD', true).isValid()
            )
            .matches(
              DATE_PATTERN,
              'Recepto galiojimo pabaigos data gali būti sudaryta tik iš skaitmenų ir brūkšnelio.'
            )
            .required('Pasirinkite recepto galiojimo pabaigos datą.')
        : schema
  ),

  activeIngredient: Yup.string()
    .strict()
    .trim(
      'Vaisto veikliosios medžiagos pavadinimo pradžioje ar pabaigoje negali būti tarpų.'
    )
    .required('Įveskite vaisto veikliąją medžiagą.')
    .max(
      ACTIVE_INGREDIENT_LENGTH_MAX,
      `Vaistio viekliosios medžiagos pavadinimas negali būti ilgesnis nei 
      ${ACTIVE_INGREDIENT_LENGTH_MAX} simbolių.`
    )
    .matches(
      ACTIVE_INGREDIENT_PATTERN,
      `Vaisto veikliosios medžiagos gali būti sudaryta tik iš lietuviškos abėcėlės raidžių, 
      skaitmenų, brūkšnelių, pasvirųjų brūkšnių, kablelių, dvitaškių, taškų, skliaustelių ir tarpų.`
    ),

  activeIngredientQuantity: Yup.string()
    .test(
      'isLessThanMax',
      `Veiklios medžiagos kiekis negali pasiekti ar viršyti 
      ${addThousandsSeparators(ACTIVE_INGREDIENT_QUANTITY_MAX_THRESHOLD)}.`,
      value =>
        value == null ||
        !POSITIVE_DECIMAL_NUMBERS_PATTERN.test(value) ||
        (value.includes(',')
          ? value.substr(0, value.indexOf(',')).length <=
            ACTIVE_INGREDIENT_QUANTITY_INTEGER_DIGITS_MAX
          : value.length <= ACTIVE_INGREDIENT_QUANTITY_INTEGER_DIGITS_MAX)
    )
    .required('Įveskite veikliosios medžiagos kiekį.')
    .matches(
      POSITIVE_DECIMAL_NUMBERS_PATTERN,
      `Veikliosios medžiagos kiekis turi būti teigiamas skaičius su maksimaliai trimis skaitmenimis
      po kablelio.`
    ),

  usageDescription: Yup.string()
    .required('Įveskite vartojimo aprašymą.')
    .max(
      DESCRIPTION_LENGTH_MAX,
      `Aprašymas negali būti ilgesnis nei ${DESCRIPTION_LENGTH_MAX} simbolių.`
    ),
});

class MedicalPrescriptionCreationFormContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  handleGoBackButtonClick = () => {
    this.context.router.goBack();
  };

  render() {
    return (
      <Formik
        enableReinitialize
        initialValues={{
          personalIdentificationNumber:
            (this.props.location.state &&
              this.props.location.state.personalIdentificationNumber) ||
            '',
          hasUnlimitedValidity: false,
          validityEndDate: '',
          activeIngredient: '',
          activeIngredientQuantity: '',
          activeIngredientMeasurementUnit: 'mg',
          usageDescription: '',
        }}
        onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
          axios
            .post(
              getRequestUrl(
                '/api/patients/filter/personal-identification-number'
              ),
              {
                personalIdentificationNumber:
                  values.personalIdentificationNumber,
              },
              getAuthorizationConfig()
            )
            .then(response => {
              const patientId = response.data.id;

              if (patientId) {
                axios
                  .post(
                    getRequestUrl(
                      `/api/patients/${patientId}/medical-prescriptions`
                    ),
                    {
                      hasUnlimitedValidity: values.hasUnlimitedValidity,
                      validityEndDate: values.hasUnlimitedValidity
                        ? null
                        : values.validityEndDate,
                      activeIngredient: values.activeIngredient,
                      activeIngredientQuantity: values.activeIngredientQuantity.replace(
                        ',',
                        '.'
                      ),
                      activeIngredientMeasurementUnit: values.activeIngredientMeasurementUnit.toUpperCase(),
                      usageDescription: values.usageDescription,
                    },
                    getAuthorizationConfig()
                  )
                  .then(() => {
                    resetForm();
                    setStatus({
                      serverValidationState: 'success',
                      serverError: {},
                      isAlertVisible: true,
                    });
                  })
                  .catch(() => {
                    setSubmitting(false);
                    setStatus({
                      serverValidationState: 'unknownError',
                      serverError: {},
                      isAlertVisible: true,
                    });
                  });
              } else {
                setSubmitting(false);
                setStatus({
                  serverValidationState: 'knownError',
                  serverError: {
                    personalIdentificationNumber: `Paciento su tokiu asmens kodu nebuvo rasta.
                      Jeigu manote, kad pacientas su tokiu asmens kodu turėtų egzistuoti „Medika“
                      sistemoje, susisiekite su administratoriumi paštu ${ADMINISTRATOR_EMAIL}
                      arba telefonu ${ADMINISTRATOR_PHONE_NUMBER}.`,
                    value: values.personalIdentificationNumber,
                  },
                  isAlertVisible: false,
                });
              }
            })
            .catch(() => {
              setSubmitting(false);
              setStatus({
                serverValidationState: 'unknownError',
                serverError: {},
                isAlertVisible: true,
              });
            });
        }}
        validationSchema={validationSchema}
        render={({
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
          <MedicalPrescriptionCreationFormComponent
            hasChanged={hasChanged}
            errors={errors}
            handleSubmit={handleSubmit}
            isSubmitting={isSubmitting}
            setStatus={setStatus}
            status={status}
            touched={touched}
            values={values}
            setFieldValue={setFieldValue}
            hasGoBackButton={!!this.props.location.state}
            handleGoBackButtonClick={this.handleGoBackButtonClick}
          />
        )}
      />
    );
  }
}

export default MedicalPrescriptionCreationFormContainer;
