package dev.abhishek.ecommerce.modules.Image.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UploadMultipleImagesDto {

    @NotNull
    private Long productId;

    @NotEmpty
    private List<MultipartFile> files;
}
