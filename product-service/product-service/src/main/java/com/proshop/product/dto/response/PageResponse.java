package com.proshop.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageResponse {

  @JsonProperty("total_pages")
  private Integer totalPages;

  @JsonProperty("has_next")
  private Boolean hasNext;

  @JsonProperty("has_previous")
  private Boolean hasPrevious;

  @JsonProperty("current_page")
  private Integer currentPage;

  @JsonProperty("total_elements")
  private Long totalElements;

  public PageResponse(Integer totalPages, Boolean hasNext, Boolean hasPrevious, Integer currentPage,
      Long totalElements) {
    this.totalPages = totalPages;
    this.hasNext = hasNext;
    this.hasPrevious = hasPrevious;
    this.currentPage = currentPage;
    this.totalElements = totalElements;
  }

}
