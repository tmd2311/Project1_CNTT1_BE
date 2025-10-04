package com.proshop.product.dto.response;


import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.exceptionlib.enums.ResErrorCode;
import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponseUtil {

  public static <T> PageResponse<T> buildPageResponse(Page<T> page) {
    int currentPage = page.getNumber() + 1; // Spring Data Page bắt đầu từ 0
    return new PageResponse<>(
        page.getContent(),
        page.getTotalPages(),
        page.hasNext(),
        page.hasPrevious(),
        currentPage,
        page.getTotalElements()
    );
  }

  public static <T> PageResponse<T> buildPageResponse(List<T> content, long totalElements, int pageNum, int pageSize) {
    int totalPage = (int) (totalElements / pageSize);
    if ((long) totalPage * pageSize < totalElements) {
      totalPage += 1;
    }
    if (pageSize > totalElements) {
      totalPage = 1;
    }

    if (pageNum < 0 || pageNum >= totalPage) {
      throw new ResException(ResErrorCode.BAD_REQUEST);
    }

    boolean hasPrevious = pageNum > 0;
    boolean hasNext = pageNum < totalPage - 1;

    return new PageResponse<>(
        content,
        totalPage,
        hasNext,
        hasPrevious,
        pageNum + 1, // currentPage
        totalElements
    );
  }
}
