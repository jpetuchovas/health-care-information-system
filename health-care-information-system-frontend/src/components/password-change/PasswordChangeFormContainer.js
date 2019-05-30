import axios from 'axios';
import { Formik } from 'formik';
import { state as globalState } from 'lape';
import React from 'react';
import Yup from 'yup';

import { JWT_REFRESH_TIME_IN_MILLISECONDS } from '../../common/constants';
import { setJwt } from '../../common/jwtUtils';
import {
  getAuthorizationConfig,
  getRequestUrl,
} from '../../common/requestUtils';
import {
  passwordConfirmationValidationSchema,
  passwordValidationSchema,
} from '../../common/validation';
import PasswordChangeFormComponent from './PasswordChangeFormComponent';

const validationSchema = Yup.object().shape({
  oldPassword: Yup.string().required('Įveskite dabartinį slaptažodį.'),
  newPassword: passwordValidationSchema('naują'),
  newPasswordConfirmation: passwordConfirmationValidationSchema(
    'newPassword',
    'naują'
  ),
});

const PasswordChangeFormContainer = () => (
  <Formik
    initialValues={{
      oldPassword: '',
      newPassword: '',
      newPasswordConfirmation: '',
    }}
    onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
      axios
        .post(
          getRequestUrl('/api/password-change'),
          {
            oldPassword: values.oldPassword,
            newPassword: values.newPassword,
          },
          getAuthorizationConfig()
        )
        .then(response => {
          clearTimeout(globalState.timeoutId);
          setJwt(response.data.token, JWT_REFRESH_TIME_IN_MILLISECONDS);

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
            error.response && error.response.status === 401;

          if (isConflictStatus) {
            setStatus({
              serverValidationState: 'knownError',
              serverError: {
                oldPassword:
                  'Įvestas slaptažodis nesutampa su dabartiniu slaptažodžiu.',
                value: values.oldPassword,
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
    render={PasswordChangeFormComponent}
  />
);

export default PasswordChangeFormContainer;
