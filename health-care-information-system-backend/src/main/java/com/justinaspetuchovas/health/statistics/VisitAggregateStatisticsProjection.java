package com.justinaspetuchovas.health.statistics;

/**
 * Doctor's aggregate visit statistics for a specific time period.
 */
public interface VisitAggregateStatisticsProjection {
  /**
   * Returns the total number of patients a doctor saw during the specified period.
   *
   * @return the total visit count
   */
  long getVisitAggregateCount();

  /**
   * Returns the sum of durations in minutes of all visits that happened during the
   * specified period.
   *
   * @return the sum of durations in minutes of all visits
   */
  long getVisitDurationInMinutesAggregateSum();
}
