package dev.abhishek.ecommerce.modules.image.service;

import dev.abhishek.ecommerce.modules.image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.image.dtos.UploadMultipleImagesDto;

import java.util.List;

public interface ImageService {

    ImageDto uploadImageToProduct(UploadImageDto uploadImageDto);

    List<ImageDto> uploadImagesToProduct(UploadMultipleImagesDto uploadMultipleImagesDto);

    List<ImageDto> getImagesOfProduct(Long productId);

    void deleteImage(Long imageId);
}
