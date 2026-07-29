package dev.abhishek.ecommerce.modules.image.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String downloadUrl;
    private Long productId;
}
