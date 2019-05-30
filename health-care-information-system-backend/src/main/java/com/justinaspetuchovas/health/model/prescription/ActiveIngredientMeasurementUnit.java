package com.justinaspetuchovas.health.model.prescription;

/**
 * Measurement unit type of a medical prescription's active ingredient.
 */
public enum ActiveIngredientMeasurementUnit {
  MG, MCG, IU;

  @Override
  public String toString() {
    return name().equals("IU") ? name() : name().toLowerCase();
  }
}
