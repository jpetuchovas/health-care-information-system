package com.justinaspetuchovas.health.model.user.pharmacist;

import com.justinaspetuchovas.health.common.ValidationConstants;
import com.justinaspetuchovas.health.model.user.NewUserCommand;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request to register a new pharmacist.
 */
public class NewPharmacistCommand extends NewUserCommand {
  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.WORKPLACE_LENGTH_MAX,
      message = "Workplace must be between {min} and {max} characters long."
  )
  @Pattern.List({
      @Pattern(
          regexp = ValidationConstants.NO_LEADING_OR_TRAILING_WHITESPACE_PATTERN,
          message = "Workplace cannot contain leading or trailing whitespace characters."
      ),
      @Pattern(
          regexp = ValidationConstants.WORKPLACE_START_PATTERN,
          message = "Workplace must start with VšĮ, UAB, AB or MB."
      ),
      @Pattern(
          regexp = ValidationConstants.LITHUANIAN_ALPHABET_WITH_HYPHEN_AND_DIGITS_PATTERN,
          message = "Workplace can only contain lithuanian alphabet characters, digits, hyphens "
              + "and spaces."
      )
  })
  private String workplace;

  public String getWorkplace() {
    return workplace;
  }

  public void setWorkplace(String workplace) {
    this.workplace = workplace;
  }
}
