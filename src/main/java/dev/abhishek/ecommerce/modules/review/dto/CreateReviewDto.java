package dev.abhishek.ecommerce.modules.review.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDto {
    @NotNull
    private Long productId;

    @NotNull
    private UUID orderId;

    @Size(max = 300)
    private String reviewMessage;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
}
