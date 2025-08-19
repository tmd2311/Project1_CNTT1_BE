package com.proshop.auth.dto.response;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {

  public static <T> ResponseEntity<GeneralResponse<T>> success() {
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    responseObject.setStatus(ResponseStatus.SUCCESS_STATUS);
    return ResponseEntity.ok(responseObject);
  }

  public static <T> GeneralResponse<T> getResponseObject(T data) {
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResponseStatus.SUCCESS_CODE);
    responseStatus.setMessage(ResponseStatus.SUCCESS_MESSAGE);
    responseStatus.setLabel(ResponseStatus.SUCCESS_LABEL);
    responseObject.setStatus(responseStatus);
    responseObject.setData(data);
    return responseObject;
  }

  public static <T> ResponseEntity<GeneralResponse<T>> success(T data) {
    return ResponseEntity.ok(getResponseObject(data));
  }

  public static <T> ResponseEntity<GeneralResponse<T>> success(Object data, Class<T> clazz) {
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResponseStatus.SUCCESS_CODE);
    responseStatus.setMessage(ResponseStatus.SUCCESS_MESSAGE);
    responseStatus.setLabel(ResponseStatus.SUCCESS_LABEL);
    responseObject.setStatus(responseStatus);
    responseObject.setData(clazz.cast(data));
    return ResponseEntity.ok(responseObject);
  }

  public static <T> ResponseEntity<GeneralResponse<T>> success(T data,
      Map<String, Object> extraData) {
    GeneralResponse<T> res = getResponseObject(data);
    res.setExtraData(extraData);
    return ResponseEntity.ok(res);
  }


  public static <T> ResponseEntity<GeneralResponse<T>> permissionDenied(String messages) {
    GeneralResponse<T> responseObject = new GeneralResponse<>();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResponseStatus.PERMISSION_DENIED_CODE);
    responseStatus.setLabel(ResponseStatus.PERMISSION_DENIED_LABEL);
    String messDisplay = ResponseStatus.PERMISSION_DENIED_MESSAGES;
    if (messages != null) {
      messDisplay = messDisplay + "." + messages;
    }
    responseStatus.setMessage(messDisplay);
    responseObject.setStatus(responseStatus);
    return new ResponseEntity<>(responseObject, HttpStatus.FORBIDDEN);
  }

  public static <T> ResponseEntity<GeneralResponse<T>> permissionDenied() {
    return permissionDenied(null);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> pagePermissionDenied() {
    GeneralPageResponse<T> responseObject = new GeneralPageResponse<>();
    ResponseStatus responseStatus = new ResponseStatus();
    responseStatus.setCode(ResponseStatus.PERMISSION_DENIED_CODE);
    responseStatus.setLabel(ResponseStatus.PERMISSION_DENIED_LABEL);
    String messDisplay = ResponseStatus.PERMISSION_DENIED_MESSAGES;
    responseStatus.setMessage(messDisplay);
    responseObject.setStatus(responseStatus);
    return ResponseEntity.ok(responseObject);
  }


  public static <T> ResponseEntity<GeneralPageResponse<T>> success(List<T> data,
      PageResponse pageResponse) {
    GeneralPageResponse<T> responseObject = new GeneralPageResponse<>(ResponseStatus.SUCCESS_STATUS,
        data,
        pageResponse);
    return ResponseEntity.ok(responseObject);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> makePaginationResponse(Page<T> page) {
    List<T> collect = page.getContent();
    PageResponse pageResponse = PageResponseUtil.buildPageResponse(page);
    GeneralPageResponse<T> responseObject = new GeneralPageResponse<>(ResponseStatus.SUCCESS_STATUS,
        collect, pageResponse);
    return ResponseEntity.ok(responseObject);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> makePaginationResponse(Page<T> page,
      List<T> data) {
    List<T> collect = page.getContent();
    PageResponse pageResponse = PageResponseUtil.buildPageResponse(page);
    GeneralPageResponse<T> responseObject = new GeneralPageResponse<>(ResponseStatus.SUCCESS_STATUS,
        collect, pageResponse);
    responseObject.setData(data);
    return ResponseEntity.ok(responseObject);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> makePaginationResponse(Page<T> page,
      Map<String, Object> extraData) {
    List<T> collect = page.getContent();
    PageResponse pageResponse = PageResponseUtil.buildPageResponse(page);
    GeneralPageResponse<T> responseObject = new GeneralPageResponse<>(ResponseStatus.SUCCESS_STATUS,
        collect, pageResponse);
    responseObject.setExtraData(extraData);
    return ResponseEntity.ok(responseObject);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> makePaginationResponse(
      GeneralPageResponse<T> responseObject, Map<String, Object> extraData) {
    responseObject.setExtraData(extraData);
    return ResponseEntity.ok(responseObject);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> makePaginationResponse(List<T> data,
      Pageable pageable, int count) {
    final Page<T> pageResult = new PageImpl<>(data, pageable, count);
    return makePaginationResponse(pageResult);
  }

  public static <T> ResponseEntity<GeneralPageResponse<T>> makePaginationResponse(List<T> data,
      Pageable pageable) {
    return makePaginationResponse(data, pageable, data.size());
  }


}