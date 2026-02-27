package dev.abhishek.ecommerce.modules.Image.dtos;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UploadMultipleImagesDto {

    @NotNull
    private Long id;

    @NotNull
    private List<MultipartFile> files;
}
