package com.shizzy.moneytransfer.s3;

import com.shizzy.moneytransfer.exception.InvalidFileFormatException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.s3.buckets.wavesend}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private final S3Client s3Client;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final String [] acceptedFileExtensions = {".pdf", ".doc", ".png", ".jpg", ".jpeg"};
    private final String [] acceptedProfileImageExtensions = {".jpg", ".jpeg", ".png"};


    public void validateProfileImage(MultipartFile file) throws InvalidFileFormatException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        boolean isValidExtension = false;
        for (String extension : acceptedProfileImageExtensions) {
            if (fileName.endsWith(extension)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new InvalidFileFormatException("Profile image must be a JPG, JPEG, or PNG");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must not exceed 10 MB");
        }
    }

    public void validateKycDocument(MultipartFile file) throws InvalidFileFormatException {
        String fileName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        boolean isValidExtension = false;
        for (String extension : acceptedFileExtensions) {
            if (fileName.endsWith(extension)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new InvalidFileFormatException("KYC document must be a PDF, DOC, PNG, JPG, or JPEG");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileFormatException("File size must not exceed 10 MB");
        }
    }

    public String uploadFileToS3(@NotNull MultipartFile file, String key) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return generateFileUrl(key);
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3 "+ e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }

    public byte[] getFileFromS3(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            try (InputStream inputStream = s3Client.getObject(getObjectRequest);
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                return byteArrayOutputStream.toByteArray();
            }
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                throw new RuntimeException("File not found in S3 with key: " + key);
            } else {
                throw new RuntimeException("Failed to retrieve file from S3", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file from S3", e);
        }
    }

//    private String generateFileUrl(String key) {
//        S3Presigner presigner = S3Presigner.builder().region(Region.of(region)).build();
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(java.time.Duration.ofMinutes(60))
//                .getObjectRequest(b -> b.bucket(bucketName).key(key))
//                .build();
//
//        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
//        presigner.close();
//
//        return presignedUrl;
//    }

    private String generateFileUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
    }
}