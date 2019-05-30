import PropTypes from 'prop-types';
import React from 'react';

import SubmitAlert from '../common/form/alert/SubmitAlert';
import UnknownErrorSubmitAlert from '../common/form/alert/UnknownErrorSubmitAlert';
import SubmitButton from '../common/form/button/SubmitButton';
import TextFieldWithGlyphicon from '../common/form/text-input/TextFieldWithGlyphicon';

const LoginFormComponent = ({
  hasChanged,
  handleSubmit,
  isSubmitting,
  status,
  setStatus,
  errors,
  touched,
}) => (
  <form onSubmit={handleSubmit}>
    <TextFieldWithGlyphicon
      name="username"
      type="text"
      glyph="user"
      placeholder="Vartotojo vardas"
      status={status}
      errors={errors}
      touched={touched}
    />

    <TextFieldWithGlyphicon
      name="password"
      type="password"
      glyph="lock"
      placeholder="Slaptažodis"
      status={status}
      errors={errors}
      touched={touched}
    />

    {status &&
    status.serverValidationState === 'knownError' &&
    status.isAlertVisible ? (
      <SubmitAlert
        type="danger"
        text="Neteisingas vartotojo vardas arba slaptažodis. Bandykite dar kartą."
        handleDismiss={() => setStatus({ ...status, isAlertVisible: false })}
      />
    ) : null}

    {status &&
    status.serverValidationState === 'unknownError' &&
    status.isAlertVisible ? (
      <UnknownErrorSubmitAlert
        handleDismiss={() => setStatus({ ...status, isAlertVisible: false })}
      />
    ) : null}

    <div className="text-center">
      <SubmitButton
        text="Prisijungti"
        isSubmitting={isSubmitting}
        isSubmittingText="Jungiamasi..."
      />
    </div>
  </form>
);

LoginFormComponent.propTypes = {
  hasChanged: PropTypes.bool.isRequired,
  handleSubmit: PropTypes.func.isRequired,
  isSubmitting: PropTypes.bool.isRequired,
  status: PropTypes.string,
  setStatus: PropTypes.func.isRequired,
};

export default LoginFormComponent;
