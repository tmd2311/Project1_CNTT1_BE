package com.proshop.file.controller;

import com.proshop.file.dto.response.FileUploadResponse;
import com.proshop.file.dto.response.GeneralResponse;
import com.proshop.file.service.FileService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @PostMapping("/upload")
  public ResponseEntity<GeneralResponse<FileUploadResponse>> uploadFile(
      @RequestParam("file") MultipartFile file) {
    FileUploadResponse uploaded = fileService.uploadFile(file);
    GeneralResponse<FileUploadResponse> response = new GeneralResponse<>();
    response.setData(uploaded);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/upload-multiple")
  public ResponseEntity<GeneralResponse<List<FileUploadResponse>>> uploadMultipleFiles(
      @RequestParam("files") MultipartFile[] files) {

    List<FileUploadResponse> uploaded = new ArrayList<>();
    for (MultipartFile file : files) {
      uploaded.add(fileService.uploadFile(file));
    }

    GeneralResponse<List<FileUploadResponse>> response = new GeneralResponse<>();
    response.setData(uploaded);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/delete/{fileName}")
  public ResponseEntity<GeneralResponse<Void>> deleteFile(@PathVariable String fileName) {
    fileService.deleteFile(fileName);
    GeneralResponse<Void> response = new GeneralResponse<>();
    response.setData(null);
    return ResponseEntity.ok(response);
  }
}
