package com.mano.Farafina_Backend.controller;

import com.mano.Farafina_Backend.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final S3Service s3Service;

    @Autowired
    public FileUploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No image file provided"));
            }

            String url = s3Service.uploadImage(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", url);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to upload image",
                            "details", e.getMessage()));
        }
    }

    @PostMapping("/upload-video")
    public ResponseEntity<?> uploadVideo(@RequestParam("video") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No video file provided"));
            }

            String url = s3Service.uploadVideo(file);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("url", url);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Failed to upload video",
                            "details", e.getMessage()));
        }
    }
}