import axios from 'axios';
import React from 'react';
import Yup from 'yup';

import { SPECIALIZATION_LENGTH_MAX } from '../../../../common/constants';
import Formik from '../../../common/form/FormikWithHasChanged';
import {
  getRequestUrl,
  getAuthorizationConfig,
} from '../../../../common/requestUtils';
import {
  getFirstNameValidationSchema,
  getLastNameValidationSchema,
  LITHUANIAN_ALPHABET_WITH_HYPHEN_PATTERN,
  passwordConfirmationValidationSchema,
  passwordValidationSchema,
  usernameValidationSchema,
} from '../../../../common/validation';
import DoctorRegistrationFormComponent from './DoctorRegistrationFormComponent';

const validationSchema = Yup.object().shape({
  doctorFirstName: getFirstNameValidationSchema('gydytojo'),
  doctorLastName: getLastNameValidationSchema('gydytojo'),
  specialization: Yup.string(),

  otherSpecialization: Yup.string()
    .strict()
    .when(
      'specialization',
      (specialization, schema) =>
        specialization === 'Kitas'
          ? schema
              .trim('Specializacijos pradžioje ar pabaigoje negali būti tarpų.')
              .required('Pasirinkite specializaciją.')
              .max(
                SPECIALIZATION_LENGTH_MAX,
                `Specializacija negali būti ilgesnė nei ${SPECIALIZATION_LENGTH_MAX} simbolių.`
              )
              .matches(
                LITHUANIAN_ALPHABET_WITH_HYPHEN_PATTERN,
                'Specializacija gali būti sudaryta tik iš lietuviškos abėcėlės raidžių, brūkšnelių ir tarpų.'
              )
          : schema
    ),

  doctorUsername: usernameValidationSchema,
  doctorPassword: passwordValidationSchema('vartotojo'),
  doctorPasswordConfirmation: passwordConfirmationValidationSchema(
    'doctorPassword',
    'vartotojo'
  ),
});

const DoctorRegistrationFormContainer = () => (
  <Formik
    initialValues={{
      doctorFirstName: '',
      doctorLastName: '',
      specialization: 'Akušeris ginekologas',
      otherSpecialization: '',
      doctorUsername: '',
      doctorPassword: '',
      doctorPasswordConfirmation: '',
    }}
    onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
      axios
        .post(
          getRequestUrl('/api/doctors'),
          {
            firstName: values.doctorFirstName,
            lastName: values.doctorLastName,
            specialization:
              values.specialization === 'Kitas'
                ? values.otherSpecialization
                : values.specialization,
            username: values.doctorUsername,
            password: values.doctorPassword,
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
            setStatus({
              serverValidationState: 'knownError',
              serverError: {
                doctorUsername:
                  'Toks vartotojo vardas jau užimtas. Pasirinkite kitokį vartotojo vardą.',
                value: values.doctorUsername,
              },
              isAlertVisible: false,
            });
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
    render={DoctorRegistrationFormComponent}
  />
);

export default DoctorRegistrationFormContainer;
