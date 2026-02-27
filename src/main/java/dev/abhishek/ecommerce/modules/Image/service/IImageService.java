package dev.abhishek.ecommerce.modules.Image.service;

import dev.abhishek.ecommerce.modules.Image.dtos.UploadImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageService {

    void uploadImage(UploadImageDto uploadImageDto) throws Exception;
}
