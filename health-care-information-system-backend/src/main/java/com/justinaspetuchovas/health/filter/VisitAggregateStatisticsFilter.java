package com.justinaspetuchovas.health.filter;

import java.util.Date;

/**
 * Filter used to get a doctor's aggregate visit statistics for the specified period.
 */
public class VisitAggregateStatisticsFilter implements VisitStatisticsFilter {
  private Date startDate;
  private Date endDate;

  @Override
  public Date getStartDate() {
    return startDate;
  }

  @Override
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  @Override
  public Date getEndDate() {
    return endDate;
  }

  @Override
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
