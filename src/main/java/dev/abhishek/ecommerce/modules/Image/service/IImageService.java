package dev.abhishek.ecommerce.modules.Image.service;

import org.springframework.web.multipart.MultipartFile;

public interface IImageService {

    void uploadImage(Long productId, MultipartFile file);
}
