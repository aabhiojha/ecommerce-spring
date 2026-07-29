package dev.abhishek.ecommerce.common.storage;

import dev.abhishek.ecommerce.common.helpers.FileNameUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RustFsStorageService implements StorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/avif");

    private final S3Client s3Client;
    private final StorageProperties properties;

    @PostConstruct
    void ensureBucketExists() {
        if (!properties.isAutoCreateBucket()) {
            return;
        }

        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(properties.getBucket()).build());
            log.info("RustFS bucket {} is available", properties.getBucket());
        } catch (NoSuchBucketException ex) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(properties.getBucket()).build());
            log.info("RustFS bucket {} created", properties.getBucket());
        } catch (S3Exception ex) {
            // Startup must not depend on RustFS being up; uploads will surface the failure instead.
            log.warn("Could not verify RustFS bucket {} at {}: {}",
                    properties.getBucket(), properties.getEndpoint(), ex.getMessage());
        }
    }

    @Override
    public String upload(String keyPrefix, MultipartFile file) {
        validate(file);

        String key = buildKey(keyPrefix, file.getOriginalFilename());
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(properties.getBucket())
                            .key(key)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException ex) {
            throw new StorageException("Could not read uploaded file: " + file.getOriginalFilename(), ex);
        } catch (S3Exception ex) {
            throw new StorageException("Could not store file in RustFS: " + key, ex);
        }

        log.debug("Stored object {} in RustFS bucket {}", key, properties.getBucket());
        return key;
    }

    @Override
    public void delete(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .build());
            log.debug("Deleted object {} from RustFS bucket {}", key, properties.getBucket());
        } catch (S3Exception ex) {
            throw new StorageException("Could not delete file from RustFS: " + key, ex);
        }
    }

    @Override
    public byte[] download(String key) {
        try {
            ResponseBytes<GetObjectResponse> object = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(properties.getBucket())
                            .key(key)
                            .build());
            return object.asByteArray();
        } catch (NoSuchKeyException ex) {
            throw new StorageException("File not found in RustFS: " + key, ex);
        } catch (S3Exception ex) {
            throw new StorageException("Could not read file from RustFS: " + key, ex);
        }
    }

    @Override
    public String downloadUrl(String key) {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("%2F", "/");
        return properties.resolvedPublicUrl() + "/" + properties.getBucket() + "/" + encodedKey;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        if (file.getSize() > properties.getMaxFileSize()) {
            throw new IllegalArgumentException(
                    "Uploaded file exceeds the maximum size of " + properties.getMaxFileSize() + " bytes");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }

    private String buildKey(String keyPrefix, String originalFilename) {
        String fileName = FileNameUtils.normalize(originalFilename);
        if (keyPrefix == null || keyPrefix.isBlank()) {
            return fileName;
        }

        String prefix = keyPrefix.endsWith("/") ? keyPrefix : keyPrefix + "/";
        return prefix + fileName;
    }
}
