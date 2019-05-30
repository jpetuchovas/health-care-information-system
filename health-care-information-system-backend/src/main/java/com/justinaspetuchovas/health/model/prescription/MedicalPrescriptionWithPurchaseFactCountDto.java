package com.justinaspetuchovas.health.model.prescription;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Data transfer object for a medical prescription with the the number of times the medical
 * prescription was used to purchase a medication. It also includes whether the medical
 * prescription is valid.
 */
public class MedicalPrescriptionWithPurchaseFactCountDto {
  private UUID id;
  private String activeIngredient;
  private BigDecimal activeIngredientQuantity;
  private String activeIngredientMeasurementUnit;
  private String usageDescription;
  private Date issueDate;
  private boolean hasUnlimitedValidity;
  private Date validityEndDate;
  private String doctorFullName;
  private int purchaseFactCount;
  private boolean isValid;

  public MedicalPrescriptionWithPurchaseFactCountDto(
      UUID id,
      String activeIngredient,
      BigDecimal activeIngredientQuantity,
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit,
      String usageDescription,
      Date issueDate,
      boolean hasUnlimitedValidity,
      Date validityEndDate,
      String doctorFullName,
      int purchaseFactCount,
      boolean isValid
  ) {
    this.id = id;
    this.activeIngredient = activeIngredient;
    this.activeIngredientQuantity = activeIngredientQuantity;
    this.activeIngredientMeasurementUnit = activeIngredientMeasurementUnit.toString();
    this.usageDescription = usageDescription;
    this.issueDate = issueDate;
    this.hasUnlimitedValidity = hasUnlimitedValidity;
    this.validityEndDate = validityEndDate;
    this.doctorFullName = doctorFullName;
    this.purchaseFactCount = purchaseFactCount;
    this.isValid = isValid;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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

  public String getActiveIngredientMeasurementUnit() {
    return activeIngredientMeasurementUnit;
  }

  public void setActiveIngredientMeasurementUnit(
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit
  ) {
    this.activeIngredientMeasurementUnit = activeIngredientMeasurementUnit.toString();
  }

  public String getUsageDescription() {
    return usageDescription;
  }

  public void setUsageDescription(String usageDescription) {
    this.usageDescription = usageDescription;
  }

  public Date getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(Date issueDate) {
    this.issueDate = issueDate;
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

  public String getDoctorFullName() {
    return doctorFullName;
  }

  public void setDoctorFullName(String doctorFullName) {
    this.doctorFullName = doctorFullName;
  }

  public int getPurchaseFactCount() {
    return purchaseFactCount;
  }

  public void setPurchaseFactCount(int purchaseFactCount) {
    this.purchaseFactCount = purchaseFactCount;
  }

  public boolean getIsValid() {
    return isValid;
  }

  public void setIsValid(boolean valid) {
    isValid = valid;
  }
}
