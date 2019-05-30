import { Formik } from 'formik';
import { isEqual } from 'lodash-es';
import PropTypes from 'prop-types';
import React from 'react';

// hasChanged prop is added to Formik in order to find out if at least one
// of the form fields' values is not empty (hasChanged is true in that case).
const FormikWithHasChanged = ({ render, ...props }) => (
  <Formik
    {...props}
    render={renderProps => {
      return render({
        ...renderProps,
        hasChanged: !isEqual(renderProps.values, renderProps.initialValues),
      });
    }}
  />
);

FormikWithHasChanged.propTypes = {
  render: PropTypes.func,
};

export default FormikWithHasChanged;
