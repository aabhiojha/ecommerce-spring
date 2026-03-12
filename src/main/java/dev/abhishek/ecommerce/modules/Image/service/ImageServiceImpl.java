package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.common.helpers.FileNameUtils;
import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.Image.mapper.ImageMapper;
import dev.abhishek.ecommerce.modules.Image.repository.ImageRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final S3Client s3Client;

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;


    @Transactional
    public void uploadImageToProduct(UploadImageDto uploadImageDto) throws Exception {

        Product product = productRepository.findById(uploadImageDto.getProductId()).orElseThrow(() -> new ProductNotFoundException("The product not found with id: " + uploadImageDto.getProductId()));

        // check the product
//        if product not found make

        MultipartFile file = uploadImageDto.getFile();
        String fileName = FileNameUtils.normalize(Objects.requireNonNull(file.getOriginalFilename()));

        // S3 upload
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );
        log.debug("File uploaded to s3 on bucket: {}", bucketName);

        String downloadUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;

        try {
            Image image = Image.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .downloadUrl(downloadUrl)
                    .product(product)
                    .build();

            imageRepository.save(image);
            log.debug("Image saved to db");

        } catch (Exception ex) {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build()
            );
            log.debug("Image deleted from the s3");
            throw ex;
        }
    }

    @Transactional
    public void uploadImage(MultipartFile file) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );
        log.info("The image is uploaded to s3");
    }


    public byte[] downloadFile(String key) {
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build()
        );
        log.info("The image is retrieved from s3");
        return objectAsBytes.asByteArray();
    }
}
