package com.proshop.file.service.impl;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.file.dto.response.FileUploadResponse;
import com.proshop.file.service.FileService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

  @Value("${file.upload-dir}")
  private String uploadDir;

  @Value("${file.base-url}")
  private String baseUrl;

  @Override
  public FileUploadResponse uploadFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new ResException(ResErrorCode.FILE_EMPTY);
    }

    try {
      Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

      // Create directories if they don't exist
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
        log.info("Đã tạo thư mục upload: {}", uploadPath);
      }

      // Verify write permissions
      if (!Files.isWritable(uploadPath)) {
        log.error("Thư mục không có quyền ghi: {}", uploadPath);
        throw new ResException(ResErrorCode.FILE_UPLOAD_FAILED);
      }

      String originalName = file.getOriginalFilename();
      String ext = (originalName != null && originalName.contains("."))
          ? originalName.substring(originalName.lastIndexOf("."))
          : "";
      String uniqueName = UUID.randomUUID() + ext;
      Path target = uploadPath.resolve(uniqueName);
      Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
      String fileUrl = baseUrl + "/files/" + uniqueName;
      log.info("Upload file thành công: {}", uniqueName);
      return new FileUploadResponse(uniqueName, fileUrl);
    } catch (java.nio.file.AccessDeniedException e) {
      log.error("Lỗi quyền truy cập khi upload file vào: {}. Kiểm tra quyền thư mục và user đang chạy ứng dụng.",
          uploadDir, e);
      throw new ResException(ResErrorCode.FILE_UPLOAD_FAILED);
    } catch (IOException e) {
      log.error("Lỗi khi upload file: {}", e.getMessage(), e);
      throw new ResException(ResErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  @Override
  public void deleteFile(String fileName) {
    try {
      Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
      Files.deleteIfExists(filePath);
      log.info("Đã xóa file: {}", fileName);
    } catch (IOException e) {
      log.error("Không thể xóa file: {}", e.getMessage(), e);
      throw new ResException(ResErrorCode.FILE_DELETE_FAILED);
    }
  }
}
