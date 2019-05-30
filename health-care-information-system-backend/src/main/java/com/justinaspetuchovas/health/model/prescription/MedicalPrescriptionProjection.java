package com.justinaspetuchovas.health.model.prescription;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Partial view of a medical prescription without its purchase fact count. Used only for
 * valid patient's medical prescriptions when a user is logged in as a pharmacist.
 */
public interface MedicalPrescriptionProjection {
  UUID getId();

  String getActiveIngredient();

  BigDecimal getActiveIngredientQuantity();

  ActiveIngredientMeasurementUnit getActiveIngredientMeasurementUnit();

  String getUsageDescription();

  Date getIssueDate();

  boolean getHasUnlimitedValidity();

  Date getValidityEndDate();

  String getDoctorFullName();
}
