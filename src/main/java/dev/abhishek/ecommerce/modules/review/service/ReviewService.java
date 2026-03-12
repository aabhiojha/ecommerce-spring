package dev.abhishek.ecommerce.modules.review.service;

import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> getAllReviewOfProduct(Long productId);
    ReviewDto createReview(CreateReviewDto createReviewDto);
    void deleteReview(Long reviewId);
    List<ReviewDto> getAllUserReviews();
}
