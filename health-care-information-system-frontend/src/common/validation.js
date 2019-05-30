import Yup from 'yup';

import {
  NAME_LENGTH_MAX,
  PASSWORD_LENGTH_MAX,
  PASSWORD_LENGTH_MIN,
  USERNAME_LENGTH_MIN,
  USERNAME_LENGTH_MAX,
} from './constants';

// This method checks if the field's value matches reference field's value.
Yup.addMethod(Yup.string, 'equalTo', (reference, message) => {
  return Yup.string().test({
    name: 'equalTo',
    message: message,
    params: {
      reference: reference.path,
    },

    test(value) {
      return value == null || value === this.resolve(reference);
    },
  });
});

const capitalizeWord = word => word.charAt(0).toUpperCase() + word.slice(1);

export const getFirstNameValidationSchema = userAddress => {
  const userAddressCapitalized = capitalizeWord(userAddress);
  return (
    Yup.string()
      .strict()
      .trim(
        `${userAddressCapitalized} vardo pradžioje ar pabaigoje negali būti tarpų.`
      )
      .required(`Įveskite ${userAddress} vardą.`)
      .max(
        NAME_LENGTH_MAX,
        `${userAddressCapitalized} vardas negali būti ilgesnis nei ${NAME_LENGTH_MAX} simbolių.`
      )
      // The regular expression matches only lithuanian alphabet characters and
      // the space character.
      .matches(
        /^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž ]+$/,
        'Vardas gali būti sudarytas tik iš lietuviškos abėcėlės raidžių ir tarpų.'
      )
  );
};

export const getLastNameValidationSchema = userAddress => {
  const userAddressCapitalized = capitalizeWord(userAddress);
  return (
    Yup.string()
      .strict()
      .trim(
        `${userAddressCapitalized} pavardės pradžioje ar pabaigoje negali būti tarpų.`
      )
      .required(`Įveskite ${userAddress} pavardę.`)
      .max(
        NAME_LENGTH_MAX,
        `${userAddressCapitalized} pavardė negali būti ilgesnis nei ${NAME_LENGTH_MAX} simbolių.`
      )
      // The regular expression matches only lithuanian alphabet characters as well as
      // the space, apostrophe and hyphen characters.
      .matches(
        /^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+([-' ][a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+)*$/,
        'Pavardė gali būti sudaryta tik iš lietuviškos abėcėlės raidžių, apostrofų, brūkšnelių ir tarpų.'
      )
  );
};

export const usernameValidationSchema = Yup.string()
  .strict()
  .trim('Vartotojo vardo pradžioje ar pabaigoje negali būti tarpų.')
  .required('Įveskite vartotojo vardą.')
  .min(
    USERNAME_LENGTH_MIN,
    `Vartotojo vardą turi sudaryti bent ${USERNAME_LENGTH_MIN} simboliai.`
  )
  .max(
    USERNAME_LENGTH_MAX,
    `Vartotojo vardas negali būti ilgesnis nei ${USERNAME_LENGTH_MAX} simbolių.`
  )
  // The regular expression matches only latin alphabet characters and digits.
  .matches(
    /^[a-zA-Z0-9]+$/,
    'Vartotojo vardas gali būti sudarytas tik iš lotyniškų raidžių ir skaitmenų.'
  );

export const passwordValidationSchema = passwordDescription =>
  Yup.string()
    .required(`Įveskite ${passwordDescription} slaptažodį.`)
    .min(
      PASSWORD_LENGTH_MIN,
      `Slaptažodį turi sudaryti bent ${PASSWORD_LENGTH_MIN} simboliai.`
    )
    .max(
      PASSWORD_LENGTH_MAX,
      `Slaptažodis negali būti ilgesnis nei ${PASSWORD_LENGTH_MAX} simboliai.`
    );

export const passwordConfirmationValidationSchema = (
  passwordFieldName,
  passwordDescription
) =>
  Yup.string()
    .equalTo(
      Yup.ref(passwordFieldName),
      'Pakartotai įvestas slaptažodis turi sutapti su prieš tai įvestu slaptažodžiu.'
    )
    .required(`Pakartokite ${passwordDescription} slaptažodį.`);

// The regular expression matches only numbers.
export const PERSONAL_IDENTIFICATION_NUMBER_PATTERN = /^\d+$/;
// The regular expression matches only character sequences that start with 3, 4, 5 or 6.
export const PERSONAL_IDENTIFICATION_NUMBER_START_PATTERN = /^[3-6].*$/;
// The regular expression matches only character sequences that start with 3, 4, 5 or 6
// and allows leading whitespace.
export const PERSONAL_IDENTIFICATION_NUMBER_START_SEARCH_PATTERN = /^\s*[3-6].*$/;
// The regular expression matches only numbers and allows leading and trailing
// whitespace characters.
export const PERSONAL_IDENTIFICATION_NUMBER_SEARCH_PATTERN = /^\s*\d+\s*$/;
// The regular expression matches numbers that start with 3 or 4.
export const PERSONAL_IDENTIFICATION_FIRST_NUMBER_20TH_CENTURY_PATTERN = /^[34]/;
// The regular expression matches numbers that start with 5 or 6.
export const PERSONAL_IDENTIFICATION_FIRST_NUMBER_21ST_CENTURY_PATTERN = /^[56]/;
// The regular expression matches only lithuanian alphabet characters as well as
// the space and hyphen characters.
export const LITHUANIAN_ALPHABET_WITH_HYPHEN_PATTERN = /^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+([- ][a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+)*$/;
// The regular expression matches only lithuanian alphabet characters and digits as well as
// the space and hyphen characters.
export const LITHUANIAN_ALPHABET_WITH_HYPHEN_AND_DIGITS_PATTERN = /^[a-zA-Z0-9ĄąČčĘęĖėĮįŠšŲųŪūŽž]+([- ][a-zA-Z0-9ĄąČčĘęĖėĮįŠšŲųŪūŽž]+)*$/;

// The regular expression matches only numbers and a hyphen.
export const DATE_PATTERN = /^[\d-]+$/;

// The regular expression matches only positive integers.
export const POSITIVE_INTEGERS_PATTERN = /^[1-9]\d*$/;
// The regular expression matches positive integers or floats with a maximum of 3
// decimal places (using comma as a decimal separator).
export const POSITIVE_DECIMAL_NUMBERS_PATTERN = /^(([1-9]\d*(,\d{1,3})?)|(0,\d{0,2}[1-9]))$/;

// The regular expression checks whether the entered value conforms to the ICD-10 format.
// It does not check whether the code exists in the ICD-10 list.
export const DISEASE_CODE_PATTERN = /^[A-TV-Z][\d][A-Z\d](\.[A-Z\d]{1,4})?$/;

// The regular expression matches only lithuanian alphabet characters as well as
// the space character and characters -, /, ,, :, ., (, ).
export const ACTIVE_INGREDIENT_PATTERN = /^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž\d().]+([-/,: ][a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž\d().]+)*$/;
