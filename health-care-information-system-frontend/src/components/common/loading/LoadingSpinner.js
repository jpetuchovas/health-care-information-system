import PropTypes from 'prop-types';
import React from 'react';
import { CircleLoader } from 'react-spinners';

import { LIGHT_BLUE_COLOR } from '../../../common/constants';

const LoadingSpinner = ({ isLoading }) => (
  <CircleLoader color={LIGHT_BLUE_COLOR} loading={isLoading} />
);

LoadingSpinner.propTypes = {
  isLoading: PropTypes.bool.isRequired,
};

export default LoadingSpinner;
