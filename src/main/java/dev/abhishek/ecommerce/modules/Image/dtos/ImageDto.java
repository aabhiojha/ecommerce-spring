package dev.abhishek.ecommerce.modules.Image.dtos;

import dev.abhishek.ecommerce.modules.product.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private Long productId;
}
