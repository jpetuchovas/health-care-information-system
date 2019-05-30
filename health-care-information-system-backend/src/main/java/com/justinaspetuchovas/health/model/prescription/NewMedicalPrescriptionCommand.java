package com.justinaspetuchovas.health.model.prescription;

import com.justinaspetuchovas.health.common.ValidationConstants;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Request to create a new medical prescription.
 */
public class NewMedicalPrescriptionCommand {
  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.ACTIVE_INGREDIENT_LENGTH_MAX,
      message = "Active ingredient name must be between {min} and {max} characters long."
  )
  @Pattern.List({
      @Pattern(
          regexp = ValidationConstants.NO_LEADING_OR_TRAILING_WHITESPACE_PATTERN,
          message = "Active ingredient name cannot contain leading or trailing whitespace "
              + " characters."
      ),
      @Pattern(
          regexp = ValidationConstants.ACTIVE_INGREDIENT_PATTERN,
          message = "Active ingredient name can only contain lithuanian alphabet characters "
              + "as well as the space character and characters -, /, ,, :, ., (, )."
      )
  })
  private String activeIngredient;

  @NotNull
  @DecimalMin(ValidationConstants.ACTIVE_INGREDIENT_QUANTITY_MIN)
  @Digits(
      integer = ValidationConstants.ACTIVE_INGREDIENT_QUANTITY_PRECISION
          - ValidationConstants.ACTIVE_INGREDIENT_QUANTITY_SCALE,
      fraction = ValidationConstants.ACTIVE_INGREDIENT_QUANTITY_SCALE
  )
  private BigDecimal activeIngredientQuantity;

  @NotNull
  private ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit;

  @NotNull
  @Size(
      min = 1,
      max = ValidationConstants.DESCRIPTION_LENGTH_MAX,
      message = "Description must be between {min} and {max} characters long."
  )
  private String usageDescription;

  @NotNull
  private boolean hasUnlimitedValidity;

  @Future
  private Date validityEndDate;

  @AssertTrue(
      message = "Validity end date must be provided if the prescription does not have unlimited "
          + "validity or validity end date must be null if the prescription has unlimited validity."
  )
  private boolean isValid() {
    return hasUnlimitedValidity ? validityEndDate == null : validityEndDate != null;
  }

  public String getActiveIngredient() {
    return activeIngredient;
  }

  public void setActiveIngredient(String activeIngredient) {
    this.activeIngredient = activeIngredient;
  }

  public BigDecimal getActiveIngredientQuantity() {
    return activeIngredientQuantity;
  }

  public void setActiveIngredientQuantity(BigDecimal activeIngredientQuantity) {
    this.activeIngredientQuantity = activeIngredientQuantity;
  }

  public ActiveIngredientMeasurementUnit getActiveIngredientMeasurementUnit() {
    return activeIngredientMeasurementUnit;
  }

  public void setActiveIngredientMeasurementUnit(
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit
  ) {
    this.activeIngredientMeasurementUnit = activeIngredientMeasurementUnit;
  }

  public String getUsageDescription() {
    return usageDescription;
  }

  public void setUsageDescription(String usageDescription) {
    this.usageDescription = usageDescription;
  }

  public boolean getHasUnlimitedValidity() {
    return hasUnlimitedValidity;
  }

  public void setHasUnlimitedValidity(boolean hasUnlimitedValidity) {
    this.hasUnlimitedValidity = hasUnlimitedValidity;
  }

  public Date getValidityEndDate() {
    return validityEndDate;
  }

  public void setValidityEndDate(Date validityEndDate) {
    this.validityEndDate = validityEndDate;
  }
}
