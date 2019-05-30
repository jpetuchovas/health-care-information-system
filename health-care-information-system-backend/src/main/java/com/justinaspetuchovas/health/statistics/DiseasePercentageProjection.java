package com.justinaspetuchovas.health.statistics;

/**
 * Occurrence statistics of a specific disease.
 */
public interface DiseasePercentageProjection {
  /**
   * Returns the disease code of a disease for which its frequency percentage was calculated.
   *
   * @return the disease code of a disease
   */
  String getDiseaseCode();

  /**
   * Returns the disease's frequency specifying the percentage of its occurrences between
   * all the occurrences of various diseases.
   *
   * @return the disease's frequency percentage
   */
  float getPercentage();
}
