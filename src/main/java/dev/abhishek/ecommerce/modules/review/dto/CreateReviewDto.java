package dev.abhishek.ecommerce.modules.review.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDto {
    private Long productId;
    private String reviewMessage;
    private Integer rating;
}
