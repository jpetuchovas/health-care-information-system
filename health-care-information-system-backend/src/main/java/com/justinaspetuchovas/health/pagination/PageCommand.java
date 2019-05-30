package com.justinaspetuchovas.health.pagination;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Page request for a specific page of a certain size.
 */
public class PageCommand {
  @NotNull
  @Min(value = 0, message = "Page number must be greater than {value}.")
  private int pageNumber;

  @NotNull
  @Min(1)
  private int pageSize;

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber - 1;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
