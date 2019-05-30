package com.justinaspetuchovas.health.pagination;

import java.util.List;

/**
 * Slice of data with a list of page elements that indicates
 * whether there's a next slice available.
 */
public class SliceDto {
  private List<?> elements;
  private boolean hasNext;

  public SliceDto(List<?> elements, boolean hasNext) {
    this.elements = elements;
    this.hasNext = hasNext;
  }

  public List<?> getElements() {
    return elements;
  }

  public void setElements(List<?> elements) {
    this.elements = elements;
  }


  public boolean getHasNext() {
    return hasNext;
  }

  public void setHasNext(boolean hasNext) {
    this.hasNext = hasNext;
  }
}
