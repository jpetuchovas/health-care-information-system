package com.justinaspetuchovas.health.statistics;

/**
 * Usage statistics of a specific active ingredient.
 */
public interface ActiveIngredientUsageCountProjection {
  /**
   * Returns the active ingredient name for which its usage count was calculated.
   *
   * @return the active ingredient name
   */
  String getActiveIngredient();

  /**
   * Returns the total number of times patient's bought medication with the specified active
   * ingredient.
   *
   * @return the active ingredient's usage count
   */
  long getUsageCount();
}
