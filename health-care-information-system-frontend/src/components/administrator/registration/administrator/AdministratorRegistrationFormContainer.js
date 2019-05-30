import axios from 'axios';
import React from 'react';
import Yup from 'yup';

import Formik from '../../../common/form/FormikWithHasChanged';
import {
  getRequestUrl,
  getAuthorizationConfig,
} from '../../../../common/requestUtils';
import {
  getFirstNameValidationSchema,
  getLastNameValidationSchema,
  passwordConfirmationValidationSchema,
  passwordValidationSchema,
  usernameValidationSchema,
} from '../../../../common/validation';
import AdministratorRegistrationFormComponent from './AdministratorRegistrationFormComponent';

const validationSchema = Yup.object().shape({
  administratorFirstName: getFirstNameValidationSchema('administratoriaus'),
  administratorLastName: getLastNameValidationSchema('administratoriaus'),
  administratorUsername: usernameValidationSchema,
  administratorPassword: passwordValidationSchema('vartotojo'),
  administratorPasswordConfirmation: passwordConfirmationValidationSchema(
    'administratorPassword',
    'vartotojo'
  ),
});

const AdministratorRegistrationFormContainer = () => (
  <Formik
    initialValues={{
      administratorFirstName: '',
      administratorLastName: '',
      administratorUsername: '',
      administratorPassword: '',
      administratorPasswordConfirmation: '',
    }}
    onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
      axios
        .post(
          getRequestUrl('/api/administrators'),
          {
            firstName: values.administratorFirstName,
            lastName: values.administratorLastName,
            username: values.administratorUsername,
            password: values.administratorPassword,
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
                administratorUsername:
                  'Toks vartotojo vardas jau užimtas. Pasirinkite kitokį vartotojo vardą.',
                value: values.administratorUsername,
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
    render={AdministratorRegistrationFormComponent}
  />
);

export default AdministratorRegistrationFormContainer;
