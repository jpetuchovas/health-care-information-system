import { state as globalState } from 'lape';

import { Role } from './constants';
import {
  canJwtBeRefreshed,
  getJwt,
  getNextJwtRefreshTimeInMilliseconds,
  getRoleFromJwt,
  logIn,
} from './jwtUtils';

// Returns a user's with a specific role homepage.
export const getHomePage = role => {
  switch (role) {
    case Role.ADMIN: {
      return '/registration/patient';
    }
    case Role.DOCTOR: {
      return '/patients';
    }
    case Role.PATIENT: {
      return '/medical-information/medical-records';
    }
    case Role.PHARMACIST: {
      return '/purchase-fact-marking';
    }
    default: {
      return '/login';
    }
  }
};

// Returns a function used to redirect a user to login page if he is not logged in
// with the specified role's permissions.
export const requireRole = role => {
  return (nextState, replace) => {
    if (globalState.isLoggedIn && globalState.role === role) {
      return;
    }

    const jwt = getJwt();
    if (jwt && canJwtBeRefreshed(jwt) && getRoleFromJwt(jwt) === role) {
      logIn(jwt, getNextJwtRefreshTimeInMilliseconds(jwt));
      return;
    }

    replace('/login');
  };
};

// Returns a function used to redirect a user to login page if he is not logged in.
export const requireToBeLoggedIn = (nextState, replace) => {
  if (globalState.isLoggedIn) {
    return;
  }

  const jwt = getJwt();
  if (jwt && canJwtBeRefreshed(jwt)) {
    logIn(jwt, getNextJwtRefreshTimeInMilliseconds(jwt));
    return;
  }

  replace('/login');
};

// Returns a function used to redirect a user to his homepage if he is logged in.
export const rerouteIfLoggedIn = (nextState, replace) => {
  if (globalState.isLoggedIn) {
    replace(getHomePage(globalState.role));
  }

  const jwt = getJwt();
  if (jwt && canJwtBeRefreshed(jwt)) {
    logIn(jwt, getNextJwtRefreshTimeInMilliseconds(jwt));
    replace(getHomePage(globalState.role));
  }
};
