package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;

public interface ImageService {

    void uploadImageToProduct(UploadImageDto uploadImageDto) throws Exception;

}
