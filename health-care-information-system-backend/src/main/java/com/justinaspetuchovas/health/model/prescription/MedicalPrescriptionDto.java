package com.justinaspetuchovas.health.model.prescription;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Data transfer object for a medical prescription without its purchase fact count. Used to return
 * only valid patient's medical prescriptions when a user is logged in as a pharmacist.
 */
public class MedicalPrescriptionDto {
  private UUID id;
  private String activeIngredient;
  private BigDecimal activeIngredientQuantity;
  private String activeIngredientMeasurementUnit;
  private String usageDescription;
  private Date issueDate;
  private boolean hasUnlimitedValidity;
  private Date validityEndDate;
  private String doctorFullName;

  public MedicalPrescriptionDto(
      UUID id,
      String activeIngredient,
      BigDecimal activeIngredientQuantity,
      ActiveIngredientMeasurementUnit activeIngredientMeasurementUnit,
      String usageDescription,
      Date issueDate,
      boolean hasUnlimitedValidity,
      Date validityEndDate,
      String doctorFullName
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

  public boolean isHasUnlimitedValidity() {
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
}
