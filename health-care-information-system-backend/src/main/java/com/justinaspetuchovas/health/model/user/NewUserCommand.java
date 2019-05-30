package com.justinaspetuchovas.health.model.user;

import com.justinaspetuchovas.health.common.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request to register a new user.
 */
public abstract class NewUserCommand {
  @NotNull
  @Size(
      min = ValidationConstants.USERNAME_LENGTH_MIN,
      max = ValidationConstants.USERNAME_LENGTH_MAX,
      message = "Username must be between {min} and {max} characters long."
  )
  @Pattern(
      regexp = ValidationConstants.USERNAME_PATTERN,
      message = "Username can only contain latin alphabet characters a-z or digits 0-9."
  )
  private String username;

  @NotNull
  @Size(
      min = ValidationConstants.PASSWORD_LENGTH_MIN,
      max = ValidationConstants.PASSWORD_LENGTH_MAX,
      message = "Password must be between {min} and {max} characters long."
  )
  private String password;

  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.NAME_LENGTH_MAX,
      message = "First name must be between {min} and {max} characters long."
  )
  @Pattern.List({
      @Pattern(
          regexp = ValidationConstants.NO_LEADING_OR_TRAILING_WHITESPACE_PATTERN,
          message = "First name cannot contain leading or trailing whitespace characters."
      ),
      @Pattern(
          regexp = ValidationConstants.LITHUANIAN_ALPHABET_PATTERN,
          message = "First name can only contain lithuanian alphabet characters and spaces."
      )
  })
  private String firstName;

  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.NAME_LENGTH_MAX,
      message = "Last name must be between {min} and {max} characters long."
  )
  @Pattern.List({
      @Pattern(
          regexp = ValidationConstants.NO_LEADING_OR_TRAILING_WHITESPACE_PATTERN,
          message = "Last name cannot contain leading or trailing whitespace characters."
      ),
      @Pattern(
          regexp = ValidationConstants.LAST_NAME_PATTERN,
          message = "Last name can only contain lithuanian alphabet characters, hyphens, "
              + "apostrophes and spaces."
      )
  })
  private String lastName;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
