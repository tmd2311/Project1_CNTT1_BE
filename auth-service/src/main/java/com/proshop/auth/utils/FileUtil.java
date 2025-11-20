package com.proshop.auth.utils;

import com.proshop.auth.client.FileServiceClient;
import com.proshop.auth.dto.response.FileUploadResponse;
import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileUtil {

  private final FileServiceClient fileClient;

  public String uploadSingleImage(MultipartFile file) {
    return Optional.ofNullable(fileClient.uploadFile(file))
        .map(ResponseEntity::getBody)
        .map(GeneralResponse::getData)
        .map(FileUploadResponse::getUrl)
        .orElseThrow(() -> new ResException(ResErrorCode.FILE_UPLOAD_FAILED));
  }

  public void deleteFileByUrl(String fileUrl) {
      if (fileUrl == null || fileUrl.isBlank()) {
          return;
      }
    try {
      String fileName = extractFileNameFromUrl(fileUrl);
      fileClient.deleteFile(fileName);
    } catch (Exception e) {
      throw new ResException(ResErrorCode.FILE_DELETE_FAILED);
    }
  }

  private String extractFileNameFromUrl(String fileUrl) {
    return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
  }
}
