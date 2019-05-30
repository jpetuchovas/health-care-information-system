package com.justinaspetuchovas.health.model.user.doctor;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.user.NewUserCommand;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request to register a new doctor.
 */
public class NewDoctorCommand extends NewUserCommand {
  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.SPECIALIZATION_LENGTH_MAX,
      message = "Specialization must be between {min} and {max} characters long."
  )
  @Pattern.List({
      @Pattern(
          regexp = ValidationConstants.NO_LEADING_OR_TRAILING_WHITESPACE_PATTERN,
          message = "Specialization cannot contain leading or trailing whitespace characters."
      ),
      @Pattern(
          regexp = ValidationConstants.LITHUANIAN_ALPHABET_WITH_HYPHEN_PATTERN,
          message = "Specialization can only contain lithuanian alphabet characters, hyphens "
              + "and spaces."
      )
  })
  private String specialization;

  public String getSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }
}
