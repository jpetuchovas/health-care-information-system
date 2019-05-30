import { API_URL } from './constants';
import { getJwt } from './jwtUtils';

export const getAuthorizationConfig = () => ({
  headers: { authorization: `Bearer ${getJwt()}` },
});

export const getRequestUrl = requestUri => `${API_URL}${requestUri}`;
