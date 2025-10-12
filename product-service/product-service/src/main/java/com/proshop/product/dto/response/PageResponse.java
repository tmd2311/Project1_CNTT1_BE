package com.proshop.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

  @JsonProperty("content")
  private List<T> content;

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



}
