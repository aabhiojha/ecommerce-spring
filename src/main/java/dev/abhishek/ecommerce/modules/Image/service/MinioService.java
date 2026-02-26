package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.common.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public void uploadFile(String fileName, InputStream inputStream, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(fileName)
                        .stream(inputStream, -1, 10485760)
                        .contentType(contentType)
                        .build()
        );
    }

    public String getFileUrl(String fileName) {
        return minioConfig.getUrl() + "/" + minioConfig.getBucket() + "/" + fileName;
    }

}