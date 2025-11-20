package com.proshop.auth.client;

import com.proshop.auth.config.FeignConfig;
import com.proshop.auth.dto.response.FileUploadResponse;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth_lib.config.FeignAuthConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "file-service", url = "http://103.90.225.90:8084", configuration = {
    FeignConfig.class,
    FeignAuthConfig.class})
public interface FileServiceClient {

  @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<GeneralResponse<FileUploadResponse>> uploadFile(
      @RequestPart("file") MultipartFile file);

  @PostMapping(value = "/file/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ResponseEntity<GeneralResponse<List<FileUploadResponse>>> uploadMultipleFiles(
      @RequestPart("files") MultipartFile[] files);

  @DeleteMapping("/file/delete/{fileName}")
  ResponseEntity<GeneralResponse<Void>> deleteFile(@PathVariable("fileName") String fileName);
}
