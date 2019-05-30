import axios from 'axios';
import React from 'react';
import Yup from 'yup';

import { WORKPLACE_LENGTH_MAX } from '../../../../common/constants';
import Formik from '../../../common/form/FormikWithHasChanged';
import {
  getRequestUrl,
  getAuthorizationConfig,
} from '../../../../common/requestUtils';
import {
  getFirstNameValidationSchema,
  getLastNameValidationSchema,
  LITHUANIAN_ALPHABET_WITH_HYPHEN_AND_DIGITS_PATTERN,
  passwordConfirmationValidationSchema,
  passwordValidationSchema,
  usernameValidationSchema,
} from '../../../../common/validation';
import PharmacistRegistrationFormComponent from './PharmacistRegistrationFormComponent';

const validationSchema = Yup.object().shape({
  pharmacistFirstName: getFirstNameValidationSchema('vaistininko'),
  pharmacistLastName: getLastNameValidationSchema('vaistininko'),

  workplace: Yup.string()
    .strict()
    .trim('Darbovietės pavadinimo pradžioje ar pabaigoje negali būti tarpų.')
    .required('Įveskite darbovietės pavadinimą.')
    .max(
      WORKPLACE_LENGTH_MAX,
      `Darbovietės pavadinimas negali būti ilgesnis nei ${WORKPLACE_LENGTH_MAX} simbolių.`
    )
    .matches(
      LITHUANIAN_ALPHABET_WITH_HYPHEN_AND_DIGITS_PATTERN,
      'Darbovietės pavadinimas gali būti sudaryta tik iš lietuviškos abėcėlės raidžių, skaitmenų, brūkšnelių ir tarpų.'
    ),

  pharmacistUsername: usernameValidationSchema,
  pharmacistPassword: passwordValidationSchema('vartotojo'),
  pharmacistPasswordConfirmation: passwordConfirmationValidationSchema(
    'pharmacistPassword',
    'vartotojo'
  ),
});

const PharmacistRegistrationFormContainer = () => (
  <Formik
    initialValues={{
      pharmacistFirstName: '',
      pharmacistLastName: '',
      workplaceType: 'VšĮ',
      workplace: '',
      pharmacistUsername: '',
      pharmacistPassword: '',
      pharmacistPasswordConfirmation: '',
    }}
    onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
      axios
        .post(
          getRequestUrl('/api/pharmacists'),
          {
            firstName: values.pharmacistFirstName,
            lastName: values.pharmacistLastName,
            workplace: `${values.workplaceType} ${values.workplace}`,
            username: values.pharmacistUsername,
            password: values.pharmacistPassword,
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
                pharmacistUsername:
                  'Toks vartotojo vardas jau užimtas. Pasirinkite kitokį vartotojo vardą.',
                value: values.pharmacistUsername,
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
    render={PharmacistRegistrationFormComponent}
  />
);

export default PharmacistRegistrationFormContainer;
