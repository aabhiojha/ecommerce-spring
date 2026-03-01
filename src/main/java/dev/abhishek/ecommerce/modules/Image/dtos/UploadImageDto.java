package dev.abhishek.ecommerce.modules.Image.dtos;

import jakarta.persistence.SecondaryTable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadImageDto {

    @NotNull
    private Long productId;

    private MultipartFile file;
}
