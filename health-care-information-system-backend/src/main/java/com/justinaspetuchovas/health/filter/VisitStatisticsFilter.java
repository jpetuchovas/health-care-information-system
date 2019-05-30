package com.justinaspetuchovas.health.filter;

import java.util.Date;

/**
 * Filter used to get a doctor's visit statistics for the specified period.
 */
public interface VisitStatisticsFilter {
  Date getStartDate();

  void setStartDate(Date startDate);

  Date getEndDate();

  void setEndDate(Date endDate);
}
