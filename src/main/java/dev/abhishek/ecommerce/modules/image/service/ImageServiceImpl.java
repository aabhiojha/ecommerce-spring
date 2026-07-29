package dev.abhishek.ecommerce.modules.image.service;

import dev.abhishek.ecommerce.common.exceptions.ProductNotFoundException;
import dev.abhishek.ecommerce.common.storage.StorageService;
import dev.abhishek.ecommerce.modules.image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.image.dtos.UploadMultipleImagesDto;
import dev.abhishek.ecommerce.modules.image.entity.Image;
import dev.abhishek.ecommerce.modules.image.mapper.ImageMapper;
import dev.abhishek.ecommerce.modules.image.repository.ImageRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public ImageDto uploadImageToProduct(UploadImageDto uploadImageDto) {
        Product product = getOwnedProduct(uploadImageDto.getProductId());
        return ImageMapper.toDto(uploadSingleImageToProduct(product, uploadImageDto.getFile()));
    }

    @Override
    @Transactional
    public List<ImageDto> uploadImagesToProduct(UploadMultipleImagesDto uploadMultipleImagesDto) {
        Product product = getOwnedProduct(uploadMultipleImagesDto.getProductId());

        List<Image> savedImages = new ArrayList<>();
        for (MultipartFile file : uploadMultipleImagesDto.getFiles()) {
            savedImages.add(uploadSingleImageToProduct(product, file));
        }

        return ImageMapper.toDtoList(savedImages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageDto> getImagesOfProduct(Long productId) {
        return ImageMapper.toDtoList(imageRepository.findByProduct_Id(productId));
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ProductNotFoundException("Image not found with id: " + imageId));

        User seller = getAuthenticatedUser();
        if (!image.getProduct().getSeller().getId().equals(seller.getId())) {
            throw new AccessDeniedException("You can only manage images of your own products");
        }

        imageRepository.delete(image);
        deleteFromStorageAfterCommit(image.getFileName());
    }

    private Image uploadSingleImageToProduct(Product product, MultipartFile file) {
        String key = storageService.upload("products/" + product.getId(), file);

        Image image = Image.builder()
                .fileName(key)
                .fileType(file.getContentType())
                .downloadUrl(storageService.downloadUrl(key))
                .product(product)
                .build();

        // If the surrounding transaction rolls back the row disappears, so the object must go too.
        registerRollbackCleanup(key);
        return imageRepository.save(image);
    }

    /**
     * Only the seller who owns the product may attach images to it.
     */
    private Product getOwnedProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("The product not found with id: " + productId));

        User seller = getAuthenticatedUser();
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new AccessDeniedException("You can only manage images of your own products");
        }

        return product;
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void registerRollbackCleanup(String key) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    safeDelete(key);
                }
            }
        });
    }

    private void deleteFromStorageAfterCommit(String key) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            safeDelete(key);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                safeDelete(key);
            }
        });
    }

    private void safeDelete(String key) {
        try {
            storageService.delete(key);
        } catch (RuntimeException ex) {
            log.warn("Failed to delete object {} from RustFS; it is now orphaned", key, ex);
        }
    }
}
