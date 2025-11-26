package com.proshop.review_service.util;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.client.FileServiceClient;
import com.proshop.review_service.dto.response.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUtil {

    private final FileServiceClient fileClient;

    /**
     * Upload một file và trả về URL
     */
    public String uploadSingleImage(MultipartFile file) {
        try {
            return Optional.ofNullable(fileClient.uploadFile(file))
                    .map(ResponseEntity::getBody)
                    .map(GeneralResponse::getData)
                    .map(FileServiceClient.FileUploadResponse::getUrl)
                    .orElseThrow(() -> new ResException(ResErrorCode.FILE_UPLOAD_FAILED));
        } catch (Exception e) {
            log.error("Failed to upload single image: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload nhiều file và trả về danh sách URL
     */
    public List<String> uploadMultipleImages(List<MultipartFile> files) {
        try {
            // Filter out null and empty files
            if (files == null || files.isEmpty()) {
                return List.of();
            }

            // Filter valid files (not null and not empty)
            List<MultipartFile> validFiles = files.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .toList();

            if (validFiles.isEmpty()) {
                log.debug("No valid files to upload");
                return List.of();
            }

            MultipartFile[] fileArray = validFiles.toArray(new MultipartFile[0]);

            return Optional.ofNullable(fileClient.uploadMultipleFiles(fileArray))
                    .map(ResponseEntity::getBody)
                    .map(GeneralResponse::getData)
                    .map(list -> list.stream()
                            .map(FileServiceClient.FileUploadResponse::getUrl)
                            .toList())
                    .orElseThrow(() -> new ResException(ResErrorCode.FILE_UPLOAD_FAILED));
        } catch (Exception e) {
            log.error("Failed to upload multiple images: {}", e.getMessage(), e);
            throw new ResException(ResErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xóa file theo URL
     */
    public void deleteFileByUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            String fileName = extractFileNameFromUrl(fileUrl);
            fileClient.deleteFile(fileName);
            log.info("Deleted file: {}", fileName);
        } catch (Exception e) {
            log.warn("Failed to delete file: {}", e.getMessage());
            throw new ResException(ResErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * Extract file name từ URL
     */
    private String extractFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
}
