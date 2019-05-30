package com.justinaspetuchovas.health.filter;

import com.justinaspetuchovas.health.pagination.PageCommand;

import java.util.Date;

/**
 * Filter used to get a page of a doctor's daily visit statistics for the specified period.
 */
public class VisitDailyStatisticsFilter extends PageCommand implements VisitStatisticsFilter {
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
