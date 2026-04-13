package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.modules.Image.dtos.ImageDto;
import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import dev.abhishek.ecommerce.modules.Image.dtos.UploadMultipleImagesDto;

import java.util.List;

public interface ImageService {

    void uploadImageToProduct(UploadImageDto uploadImageDto) throws Exception;

    List<ImageDto> uploadImagesToProduct(UploadMultipleImagesDto uploadMultipleImagesDto) throws Exception;

}
