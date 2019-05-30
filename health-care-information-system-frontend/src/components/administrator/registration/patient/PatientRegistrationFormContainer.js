import axios from 'axios';
import moment from 'moment';
import React from 'react';
import Yup from 'yup';

import {
  MINIMUM_DATE,
  PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
} from '../../../../common/constants';
import Formik from '../../../common/form/FormikWithHasChanged';
import {
  getRequestUrl,
  getAuthorizationConfig,
} from '../../../../common/requestUtils';
import {
  DATE_PATTERN,
  getFirstNameValidationSchema,
  getLastNameValidationSchema,
  passwordConfirmationValidationSchema,
  passwordValidationSchema,
  PERSONAL_IDENTIFICATION_FIRST_NUMBER_20TH_CENTURY_PATTERN,
  PERSONAL_IDENTIFICATION_FIRST_NUMBER_21ST_CENTURY_PATTERN,
  PERSONAL_IDENTIFICATION_NUMBER_PATTERN,
  usernameValidationSchema,
} from '../../../../common/validation';
import PatientRegistrationFormComponent from './PatientRegistrationFormComponent';

// This method checks if field value's substring from 2nd to 7th character
// matched reference fields (which should be a date in YYYY-MM-DD format) digits.
Yup.addMethod(Yup.string, 'includesDateDigits', (reference, message) => {
  return Yup.string().test({
    name: 'includesDateDigits',
    message: message,
    params: {
      reference: reference.path,
    },

    test(value) {
      return (
        value == null ||
        this.resolve(reference) == null ||
        !PERSONAL_IDENTIFICATION_NUMBER_PATTERN.test(value) ||
        value.length !== PERSONAL_IDENTIFICATION_NUMBER_LENGTH ||
        !(
          ((this.resolve(reference).startsWith('19') ||
            this.resolve(reference).startsWith('2000')) &&
            PERSONAL_IDENTIFICATION_FIRST_NUMBER_20TH_CENTURY_PATTERN.test(
              value
            )) ||
          ((this.resolve(reference).startsWith('20') ||
            this.resolve(reference).startsWith('2100')) &&
            PERSONAL_IDENTIFICATION_FIRST_NUMBER_21ST_CENTURY_PATTERN.test(
              value
            ))
        ) ||
        (value.substring(1, 3) === this.resolve(reference).substring(2, 4) &&
          value.substring(3, 5) === this.resolve(reference).substring(5, 7) &&
          value.substring(5, 7) === this.resolve(reference).substring(8, 10))
      );
    },
  });
});

Yup.addMethod(
  Yup.string,
  'isCorrectDate',
  (
    minimumDate,
    isNotEarlierThanMessage,
    isNonFutureMessage,
    isValidMessage
  ) => {
    return Yup.string()
      .test(
        'isNotEarlierThan',
        isNotEarlierThanMessage,
        value =>
          value == null ||
          !DATE_PATTERN.test(value) ||
          !moment(value, 'YYYY-MM-DD', true).isValid() ||
          !moment(value, 'YYYY-MM-DD', true).isSameOrBefore(
            moment().format('YYYY-MM-DD')
          ) ||
          moment(value, 'YYYY-MM-DD', true).isSameOrAfter(
            moment(minimumDate).format('YYYY-MM-DD')
          )
      )
      .test(
        'isNonFutureDate',
        isNonFutureMessage,
        value =>
          value == null ||
          !DATE_PATTERN.test(value) ||
          !moment(value, 'YYYY-MM-DD', true).isValid() ||
          moment(value, 'YYYY-MM-DD', true).isSameOrBefore(
            moment().format('YYYY-MM-DD')
          )
      )
      .test(
        'isValidDate',
        isValidMessage,
        value =>
          value == null ||
          !DATE_PATTERN.test(value) ||
          moment(value, 'YYYY-MM-DD', true).isValid()
      );
  }
);

const validationSchema = Yup.object().shape({
  patientFirstName: getFirstNameValidationSchema('paciento'),
  patientLastName: getLastNameValidationSchema('paciento'),

  birthDate: Yup.string()
    .isCorrectDate(
      MINIMUM_DATE,
      `Paciento gimimo data negali būti ankstesnė negu ${MINIMUM_DATE}.`,
      'Paciento gimimo data negali būti ateityje.',
      'Įveskite egzistuojančią paciento gimimo datą formatu MMMM-mm-dd.'
    )
    .matches(
      DATE_PATTERN,
      'Paciento gimimo data gali būti sudaryta tik iš skaitmenų ir brūkšnelio.'
    )
    .required('Pasirinkite paciento gimimo datą.'),

  personalIdentificationNumber: Yup.string()
    .includesDateDigits(
      Yup.ref('birthDate'),
      'Asmens kodo 2-7 skaitmenys turi sutapti su dviem paskutiniais gimimo metų bei mėnesio ir dienos skaitmenimis.'
    )
    .when('birthDate', (birthDate, schema) => {
      const isBirthDateFilled = birthDate != null;
      if (
        isBirthDateFilled &&
        (birthDate.startsWith('19') || birthDate.startsWith('2000'))
      ) {
        return schema.matches(
          PERSONAL_IDENTIFICATION_FIRST_NUMBER_20TH_CENTURY_PATTERN,
          'XX a. gimusio žmogaus asmens kodas turi prasidėti skaitmeniu 3 arba 4.'
        );
      } else if (
        isBirthDateFilled &&
        (birthDate.startsWith('20') || birthDate.startsWith('2100'))
      ) {
        return schema.matches(
          PERSONAL_IDENTIFICATION_FIRST_NUMBER_21ST_CENTURY_PATTERN,
          'XXI a. gimusio žmogaus asmens kodas turi prasidėti skaitmeniu 5 arba 6.'
        );
      } else {
        return schema;
      }
    })
    .matches(
      PERSONAL_IDENTIFICATION_NUMBER_PATTERN,
      'Asmens kodas turi būti sudarytas tik iš skaitmenų.'
    )
    .required('Įveskite paciento asmens kodą.')
    .length(
      PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
      `Asmens kodas turi būti sudarytas iš ${PERSONAL_IDENTIFICATION_NUMBER_LENGTH} skaitmenų.`
    ),

  patientUsername: usernameValidationSchema,
  patientPassword: passwordValidationSchema('vartotojo'),
  patientPasswordConfirmation: passwordConfirmationValidationSchema(
    'patientPassword',
    'vartotojo'
  ),
});

const getKnownErrorStatus = serverError => ({
  serverValidationState: 'knownError',
  serverError: serverError,
  isAlertVisible: false,
});

const PatientRegistrationFormContainer = () => (
  <Formik
    initialValues={{
      patientFirstName: '',
      patientLastName: '',
      birthDate: '',
      personalIdentificationNumber: '',
      patientUsername: '',
      patientPassword: '',
      patientPasswordConfirmation: '',
    }}
    onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
      axios
        .post(
          getRequestUrl('/api/patients'),
          {
            firstName: values.patientFirstName,
            lastName: values.patientLastName,
            birthDate: values.birthDate,
            personalIdentificationNumber: values.personalIdentificationNumber,
            username: values.patientUsername,
            password: values.patientPassword,
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
        .catch(error => {
          setSubmitting(false);
          const isConflictStatus =
            error.response && error.response.status === 409;

          if (
            isConflictStatus &&
            error.response.data.exception.endsWith('UsernameConflictException')
          ) {
            setStatus(
              getKnownErrorStatus({
                patientUsername:
                  'Toks vartotojo vardas jau užimtas. Pasirinkite kitokį vartotojo vardą.',
                value: values.patientUsername,
              })
            );
          } else if (
            isConflictStatus &&
            error.response.data.exception.endsWith(
              'PersonalIdentificationNumberConflictException'
            )
          ) {
            setStatus(
              getKnownErrorStatus({
                personalIdentificationNumber:
                  'Pacientas su tokiu asmens kodu jau egzistuoja.',
                value: values.personalIdentificationNumber,
              })
            );
          } else {
            setStatus({
              serverValidationState: 'unknownError',
              serverError: {},
              isAlertVisible: true,
            });
          }
        });
    }}
    validationSchema={validationSchema}
    render={PatientRegistrationFormComponent}
  />
);

export default PatientRegistrationFormContainer;
