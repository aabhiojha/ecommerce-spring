package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.entity.Image;
import dev.abhishek.ecommerce.modules.Image.mapper.ImageMapper;
import dev.abhishek.ecommerce.modules.Image.repository.ImageRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final MinioService minioService;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public void uploadImageToProduct(UploadImageDto uploadImageDto) throws Exception {

        MultipartFile file = uploadImageDto.getFile();

        // minio upload
        minioService.uploadFile(
                file.getOriginalFilename(),
                file.getInputStream(),
                file.getContentType()
        );

        log.debug("File uploaded to minio");

        try {
            Product product = productRepository.findById(
                            uploadImageDto.getProductId())
                    .orElseThrow(() ->
                            new Exception("Product not found with id: " + uploadImageDto.getProductId()));
            // set the information in image table
            Image entity = ImageMapper.toEntity(uploadImageDto, product, minioService.getFileUrl(file.getOriginalFilename()));
            imageRepository.save(entity);
            log.debug("The file is saved to db.");
        } catch (Exception e) {
            log.debug("The file metadata is not saved to db.");
            minioService.deleteFile(file.getOriginalFilename());
            log.debug("The file is deleted from minio");
            log.error(e.getMessage());
        }
    }


}
