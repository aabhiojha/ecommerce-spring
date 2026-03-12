package dev.abhishek.ecommerce.modules.review.controller;

import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewDto>> getAllReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getAllReviewOfProduct(productId));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@RequestBody CreateReviewDto createReviewDto) {
        ReviewDto review = reviewService.createReview(createReviewDto);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
}
