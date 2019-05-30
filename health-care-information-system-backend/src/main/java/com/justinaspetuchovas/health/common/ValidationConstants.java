package com.justinaspetuchovas.health.common;

/**
 * Holder of constants that are used to validate data and set the column lengths in the database.
 */
public final class ValidationConstants {
  /** The minimum length a username. */
  public static final short USERNAME_LENGTH_MIN = 3;

  /** The maximum length of a username. */
  public static final short USERNAME_LENGTH_MAX = 50;

  /** The maximum length of a first or last name. */
  public static final short NAME_LENGTH_MAX = 50;

  /** The minimum length of a password. */
  public static final short PASSWORD_LENGTH_MIN = 6;

  /**
   * The maximum length of a password.
   *
   * <p>This password length is set because, even though BCrypt will
   * work with much longer passwords, only {@value #PASSWORD_LENGTH_MAX} characters are used.
   */
  public static final short PASSWORD_LENGTH_MAX = 72;

  /** The maximum length of a doctor's specialization name. */
  public static final short SPECIALIZATION_LENGTH_MAX = 100;

  /** The maximum length of pharmacist's workplace name. */
  public static final short WORKPLACE_LENGTH_MAX = 100;

  /** The length of a personal identification number. */
  public static final short PERSONAL_IDENTIFICATION_NUMBER_LENGTH = 11;

  /** The maximum length of a user's role name. */
  public static final short ROLE_LENGTH_MAX = 10;

  /** The maximum length of a medical record's or prescription's description. */
  // Integer.MAX_VALUE is the maximum size of VARCHAR data type in H2 database.
  public static final int DESCRIPTION_LENGTH_MAX = Integer.MAX_VALUE;

  /** The maximum length of an ICD-10 disease code. */
  public static final short DISEASE_CODE_LENGTH_MAX = 8;

  /** The minimum duration of a doctor's visit. */
  public static final short VISIT_DURATION_IN_MINUTES_MIN = 1;

  /** The maximum duration of a doctor's visit. */
  public static final short VISIT_DURATION_IN_MINUTES_MAX = 480;

  /** The number of digits used to denote the duration of a doctor's visit */
  public static final short VISIT_DURATION_IN_MINUTES_MAX_NUMBER_OF_DIGITS = 3;

  /** The maximum length of a medical prescription's active ingredient name. */
  public static final short ACTIVE_INGREDIENT_LENGTH_MAX = 100;

  /**
   * The minimum quantity of a medical prescription's active ingredient.
   *
   * <p>The quantity is represented as a <code>String</code> to avoid rounding errors of
   * floating-point numbers.
   */
  public static final String ACTIVE_INGREDIENT_QUANTITY_MIN = "0.001";

  /** The precision of an active ingredient's quantity. */
  public static final short ACTIVE_INGREDIENT_QUANTITY_PRECISION = 19;

  /** The scale of an active ingredient's quantity. */
  public static final short ACTIVE_INGREDIENT_QUANTITY_SCALE = 3;

  /** The maximum length of an active ingredient's measurement unit name. */
  public static final short ACTIVE_INGREDIENT_MEASUREMENT_UNIT_LENGTH_MAX = 3;

  /** A regular expression that does not allow leading or trailing whitespace characters. */
  public static final String NO_LEADING_OR_TRAILING_WHITESPACE_PATTERN = "^[\\S]+(\\s+[\\S]+)*$";

  /**
   * A regular expression that matches only lithuanian alphabet characters and the space character.
   */
  public static final String LITHUANIAN_ALPHABET_PATTERN = "^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž ]+$";

  /**
   * A regular expression that matches only lithuanian alphabet characters as well as
   * the space and hyphen characters.
   */
  public static final String LITHUANIAN_ALPHABET_WITH_HYPHEN_PATTERN =
      "^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+([- ][a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+)*$";

  /**
   * A regular expression that matches only lithuanian alphabet characters as well as
   * the space, apostrophe and hyphen characters. It is used to validate the last name
   * during a new user's registration.
   */
  public static final String LAST_NAME_PATTERN =
      "^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+([-' ][a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž]+)*$";

  /**
   * A regular expression that matches only lithuanian alphabet characters and digits as well as
   * the space and hyphen characters.
   */
  public static final String LITHUANIAN_ALPHABET_WITH_HYPHEN_AND_DIGITS_PATTERN =
      "^[a-zA-Z0-9ĄąČčĘęĖėĮįŠšŲųŪūŽž]+([- ][a-zA-Z0-9ĄąČčĘęĖėĮįŠšŲųŪūŽž]+)*$";

  /**
   * A regular expression that matches only words that start with VšĮ, UAB, AB or MB.
   * It is used to validate the workplace name during a new pharmacist's registration.
   */
  public static final String WORKPLACE_START_PATTERN = "^(VšĮ|UAB|AB|MB).*$";

  /**
   * A regular expression that matches only latin alphabet characters and digits.
   * It is used to validate the username during a new user's registration.
   */
  public static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";

  /**
   * A regular expression that checks whether the entered value conforms to the ICD-10 format.
   * It does not check whether the code exists in the ICD-10 list.
   */
  public static final String DISEASE_CODE_PATTERN = "^[A-TV-Z][\\d][A-Z\\d](\\.[A-Z\\d]{1,4})?$";

  /**
   * A regular expression that matches only lithuanian alphabet characters as well as
   * the space character and characters -, /, ,, :, ., (, ). It is used to validate
   * the active ingredient name during a new medical prescription's creation.
   */
  public static final String ACTIVE_INGREDIENT_PATTERN =
      "^[a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž\\d().]+([-/,: ][a-zA-ZĄąČčĘęĖėĮįŠšŲųŪūŽž\\d().]+)*$";

  private ValidationConstants() {
  }
}
