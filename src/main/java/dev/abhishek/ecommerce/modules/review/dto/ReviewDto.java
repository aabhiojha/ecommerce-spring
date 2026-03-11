package dev.abhishek.ecommerce.modules.review.dto;

import dev.abhishek.ecommerce.modules.product.entity.Product;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private Long userId;
    private String userName;
    private String reviewMessage;
    private Integer rating;
    private Long productId;
    private String productName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
