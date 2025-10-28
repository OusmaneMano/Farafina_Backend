package com.mano.Farafina_Backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Autowired
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = generateFileName(file, "products");
        uploadFile(file, fileName);
        return getFileUrl(fileName);
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        String fileName = generateFileName(file, "videos");
        uploadFile(file, fileName);
        return getFileUrl(fileName);
    }

    private void uploadFile(MultipartFile file, String fileName) throws IOException {
        // FIXED: Removed ACL setting since your bucket doesn't allow ACLs
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                // .acl(ObjectCannedACL.PUBLIC_READ)  // REMOVED THIS LINE
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        System.out.println("File uploaded successfully: " + fileName);
    }

    private String generateFileName(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        return folder + "/" + UUID.randomUUID() + "_" + System.currentTimeMillis() + extension;
    }

    private String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, fileName);
    }
}