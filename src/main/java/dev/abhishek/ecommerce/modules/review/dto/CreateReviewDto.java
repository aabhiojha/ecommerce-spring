package dev.abhishek.ecommerce.modules.review.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDto {
    private Long productId;
    private UUID orderId;
    private String reviewMessage;
    private Integer rating;
}
