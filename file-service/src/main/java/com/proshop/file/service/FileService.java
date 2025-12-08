package com.proshop.file.service;

import com.proshop.file.dto.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  /**
   * Upload 1 file ảnh lên hệ thống.
   * @param file File cần upload
   * @return URL truy cập file sau khi upload
   */
  FileUploadResponse uploadFile(MultipartFile file);

  /**
   * Xóa file khỏi hệ thống theo tên file
   * @param fileName tên file
   */
  void deleteFile(String fileName);
}
