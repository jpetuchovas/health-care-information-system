import axios from 'axios';
import jwtDecode from 'jwt-decode';
import { setState, state as globalState } from 'lape';

import {
  JWT_KEY,
  JWT_REFRESH_TIME_IN_MILLISECONDS,
  MINIMUM_AMOUNT_OF_TIME_LEFT_TO_ALLOW_JWT_REFRESH,
} from './constants';
import { getAuthorizationConfig, getRequestUrl } from './requestUtils';

const MILLISECONDS_IN_ONE_SECOND = 1000;

export const getJwt = () => globalState.jwt || localStorage.getItem(JWT_KEY);

export const removeJwt = () => {
  localStorage.removeItem(JWT_KEY);
  setState({
    ...globalState,
    isLoggedIn: false,
    jwt: '',
    role: '',
    userId: '',
    name: '',
    timeoutId: null,
  });
};

export const setJwt = (token, nextJwtRefreshTimeInMilliseconds) => {
  setState({ ...globalState, jwt: token });
  localStorage.setItem(JWT_KEY, token);
  const timeoutId = setTimeout(() => {
    axios
      .get(getRequestUrl('/api/refresh'), getAuthorizationConfig())
      .then(response => {
        setJwt(response.data.token, JWT_REFRESH_TIME_IN_MILLISECONDS);
      })
      .catch(() => {
        removeJwt();
      });
  }, Math.max(nextJwtRefreshTimeInMilliseconds, 0));
  setState({ ...globalState, timeoutId });
};

export const canJwtBeRefreshed = jwt =>
  jwtDecode(jwt).exp * MILLISECONDS_IN_ONE_SECOND - new Date().getTime() >=
  MINIMUM_AMOUNT_OF_TIME_LEFT_TO_ALLOW_JWT_REFRESH;

export const getNextJwtRefreshTimeInMilliseconds = jwt =>
  jwtDecode(jwt).exp * MILLISECONDS_IN_ONE_SECOND -
  new Date().getTime() -
  MINIMUM_AMOUNT_OF_TIME_LEFT_TO_ALLOW_JWT_REFRESH;

export const getRoleFromJwt = jwt => jwtDecode(jwt).role;
export const getUserIdFromJwt = jwt => jwtDecode(jwt).userId;
export const getNameFromJwt = jwt => jwtDecode(jwt).name;

export const logIn = (jwt, nextJwtRefreshTimeInMilliseconds) => {
  setJwt(jwt, nextJwtRefreshTimeInMilliseconds);
  setState({
    ...globalState,
    isLoggedIn: true,
    role: getRoleFromJwt(jwt),
    userId: getUserIdFromJwt(jwt),
    name: getNameFromJwt(jwt),
  });
};

export const logOut = () => {
  clearTimeout(globalState.timeoutId);
  removeJwt();
};
