package com.justinaspetuchovas.health.model.user;

import com.justinaspetuchovas.health.common.ValidationConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Request to change a user's password.
 */
public class PasswordChanger {
  private String oldPassword;

  @NotNull
  @Size(
      min = ValidationConstants.PASSWORD_LENGTH_MIN,
      max = ValidationConstants.PASSWORD_LENGTH_MAX,
      message = "New password must be between {min} and {max} characters long."
  )
  private String newPassword;

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
