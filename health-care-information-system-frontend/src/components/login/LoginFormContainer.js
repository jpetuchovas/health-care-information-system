import axios from 'axios';
import { state as globalState } from 'lape';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import Yup from 'yup';

import { JWT_REFRESH_TIME_IN_MILLISECONDS } from '../../common/constants';
import Formik from '../common/form/FormikWithHasChanged';
import { logIn } from '../../common/jwtUtils';
import { getRequestUrl } from '../../common/requestUtils';
import { getHomePage } from '../../common/routingUtils';
import LoginFormComponent from './LoginFormComponent';

const validationSchema = Yup.object().shape({
  username: Yup.string().required('Įveskite vartotojo vardą.'),
  password: Yup.string().required('Įveskite slaptažodį.'),
});

class LoginFormContainer extends Component {
  static contextTypes = {
    router: PropTypes.object.isRequired,
  };

  render() {
    return (
      <Formik
        initialValues={{
          username: '',
          password: '',
        }}
        onSubmit={(values, { setSubmitting, setStatus, resetForm }) => {
          axios
            .post(getRequestUrl('/api/login'), values)
            .then(response => {
              resetForm();
              setStatus({
                serverValidationState: 'success',
                isAlertVisible: false,
              });

              logIn(response.data.token, JWT_REFRESH_TIME_IN_MILLISECONDS);
              this.context.router.push(getHomePage(globalState.role));
            })
            .catch(error => {
              setSubmitting(false);
              if (error.response && error.response.status === 401) {
                setStatus({
                  serverValidationState: 'knownError',
                  isAlertVisible: true,
                });
              } else {
                setStatus({
                  serverValidationState: 'unknownError',
                  isAlertVisible: true,
                });
              }
            });
        }}
        validationSchema={validationSchema}
        validateOnBlur={false}
        render={LoginFormComponent}
      />
    );
  }
}

export default LoginFormContainer;
