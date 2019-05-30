package com.justinaspetuchovas.health.model.user.patient;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.user.NewUserCommand;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Request to register a new patient.
 */
public class NewPatientCommand extends NewUserCommand {
  @NotNull
  private Date birthDate;

  @NotNull
  @Size(
      min = ValidationConstants.PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
      max = ValidationConstants.PERSONAL_IDENTIFICATION_NUMBER_LENGTH,
      message = "Personal identification number must contain {min} digits."
  )
  // The regular expression matches only numbers.
  @Pattern(
      regexp = "^\\d+$",
      message = "Personal identification number can only contain digits."
  )
  private String personalIdentificationNumber;

  // TODO: refactor this function into multiple functions and write constants for magic numbers.
  @SuppressWarnings("checkstyle:magicnumber")
  @AssertTrue(
      message = "Personal identification code's 2nd to 7th digits must match "
          + "the last two digits of the birth year as well as the birth month and day. "
          + "The first digit of the personal identification code must be 3 or 4 if a person "
          + "is born in 20th century or 5 or 6 if a person is born in 21st century. "
          + "Birth date must be valid and also cannot be earlier than 1900-01-01."
  )
  private boolean isValid() {
    String birthDateText;
    try {
      birthDateText = new SimpleDateFormat("yyyy-MM-dd").format(birthDate);
    } catch (Exception exception) {
      return false;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.set(1900, Calendar.JANUARY, 1);
    calendar.add(Calendar.DAY_OF_YEAR, -1);
    Date dateThreshold = calendar.getTime();
    return personalIdentificationNumber != null
        && birthDate.after(dateThreshold)
        && (((birthDateText.startsWith("19") || birthDateText.startsWith("2000"))
               && (personalIdentificationNumber.startsWith("3")
                      || personalIdentificationNumber.startsWith("4")))
            || ((birthDateText.startsWith("20") || birthDateText.startsWith("2100"))
                  && (personalIdentificationNumber.startsWith("5")
                      || personalIdentificationNumber.startsWith("6"))))
        && personalIdentificationNumber.substring(1, 3).equals(birthDateText.substring(2, 4))
        && personalIdentificationNumber.substring(3, 5).equals(birthDateText.substring(5, 7))
        && personalIdentificationNumber.substring(5, 7).equals(birthDateText.substring(8, 10));
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getPersonalIdentificationNumber() {
    return personalIdentificationNumber;
  }

  public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
    this.personalIdentificationNumber = personalIdentificationNumber;
  }
}
