package dev.abhishek.ecommerce.modules.review.service;

import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;

import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReviewService {
    PagedResponse<ReviewDto> getAllReviewOfProduct(Long productId, Pageable pageable);
    ReviewDto createReview(CreateReviewDto createReviewDto);
    void deleteReview(Long reviewId);
    PagedResponse<ReviewDto> getAllUserReviews(Pageable pageable);
    void deleteReview(Long reviewId, Long user_id);
}
