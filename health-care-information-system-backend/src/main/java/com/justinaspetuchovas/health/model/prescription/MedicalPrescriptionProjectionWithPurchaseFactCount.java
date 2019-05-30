package com.justinaspetuchovas.health.model.prescription;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Partial view of a medical prescription with the the number of times the medical
 * prescriptions was used to purchase a medication.
 */
public interface MedicalPrescriptionProjectionWithPurchaseFactCount {
  UUID getId();

  String getActiveIngredient();

  BigDecimal getActiveIngredientQuantity();

  ActiveIngredientMeasurementUnit getActiveIngredientMeasurementUnit();

  String getUsageDescription();

  Date getIssueDate();

  boolean getHasUnlimitedValidity();

  Date getValidityEndDate();

  String getDoctorFullName();

  int getPurchaseFactCount();
}
