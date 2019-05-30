import { setState } from 'lape';

const defaultGlobalState = {
  isLoggedIn: false,
  jwt: '',
  role: '',
  userId: '',
  name: '',
  timeoutId: null,
};

setState(defaultGlobalState);
