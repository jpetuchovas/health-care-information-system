export const API_URL = 'http://localhost:8080';

export const JWT_KEY = 'jwt';
// Equal to 25 minutes.
const JWT_EXPIRATION_TIME_IN_MILLISECONDS = 1500000;
// Equal to 2 minutes.
export const MINIMUM_AMOUNT_OF_TIME_LEFT_TO_ALLOW_JWT_REFRESH = 120000;
// Equal to 1 minute.
const ERROR_MARGIN = 60000;
export const JWT_REFRESH_TIME_IN_MILLISECONDS =
  JWT_EXPIRATION_TIME_IN_MILLISECONDS -
  MINIMUM_AMOUNT_OF_TIME_LEFT_TO_ALLOW_JWT_REFRESH -
  ERROR_MARGIN;

export const Role = Object.freeze({
  ADMIN: 'ADMIN',
  DOCTOR: 'DOCTOR',
  PATIENT: 'PATIENT',
  PHARMACIST: 'PHARMACIST',
});

export const NAME_LENGTH_MAX = 50;
export const USERNAME_LENGTH_MIN = 3;
export const USERNAME_LENGTH_MAX = 50;
export const PASSWORD_LENGTH_MIN = 6;
export const PASSWORD_LENGTH_MAX = 72;
export const SPECIALIZATION_LENGTH_MAX = 100;
export const WORKPLACE_LENGTH_MAX = 100;
export const MINIMUM_DATE = '1900-01-01';
export const PERSONAL_IDENTIFICATION_NUMBER_LENGTH = 11;
// Maximum size of VARCHAR data type in H2 database.
export const DESCRIPTION_LENGTH_MAX = 2147483647;
export const VISIT_DURATION_IN_HOURS_MAX = 8;
export const ACTIVE_INGREDIENT_LENGTH_MAX = 100;
export const ACTIVE_INGREDIENT_QUANTITY_INTEGER_DIGITS_MAX = 16;
export const ACTIVE_INGREDIENT_QUANTITY_MAX_THRESHOLD = 10000000000000000;
export const MINIMUM_VISIT_STATISTICS_DATE = '2018-01-01';

export const LIGHT_BLUE_COLOR = '#2196f3';
export const TRANSPARENT_BLUE_COLOR = '#0c84e4a1';
export const LIGHT_GREEN_COLOR = '#8bc34a';
export const RED_COLOR = '#e51c23';

export const MAIN_CONTENT_MARGIN = '0 4.1vw';

export const ADMINISTRATOR_EMAIL = 'administrator@medika.lt';
export const ADMINISTRATOR_PHONE_NUMBER = '+370 688 00555';
