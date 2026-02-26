package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.modules.Image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private  final ImageRepository imageRepository;

    @Override
    public void uploadImage(Long productId, MultipartFile file) {

    }
}
