package com.justinaspetuchovas.health.statistics;

import java.util.Date;

/**
 * Doctor's visit statistics of a specific day.
 */
public interface VisitDailyStatisticsProjection {
  /**
   * Returns the date of the day for which the statistics were calculated.
   *
   * @return the date for which the statistics were calculated.
   */
  Date getDate();

  /**
   * Returns the total number of patients a doctor saw during the day.
   *
   * @return the total visit count
   */
  long getVisitCount();

  /**
   * Returns the sum of durations in minutes of all visits that happened during the day.
   *
   * @return the sum of durations in minutes of all visits
   */
  long getVisitDurationInMinutesSum();
}
