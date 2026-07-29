package dev.abhishek.ecommerce.modules.review.controller;

import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import dev.abhishek.ecommerce.common.dto.PagedResponse;

@Tag(name = "Reviews", description = "Endpoints for managing product reviews")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Get all reviews for a product", description = "Retrieves all reviews for a specific product")
    @GetMapping("/{productId}")
    public ResponseEntity<PagedResponse<ReviewDto>> getAllReviews(
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(reviewService.getAllReviewOfProduct(productId, PageRequest.of(pageNo, pageSize)));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get all user reviews", description = "Retrieves all reviews written by the current user")
    @GetMapping("/user")
    public ResponseEntity<PagedResponse<ReviewDto>> getAllUserReview(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(reviewService.getAllUserReviews(PageRequest.of(pageNo, pageSize)));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create review", description = "Creates a new review for a product")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody CreateReviewDto createReviewDto) {
        return new ResponseEntity<>(reviewService.createReview(createReviewDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Delete review", description = "Deletes a specific review by the current user")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete review (Admin)", description = "Deletes a specific review on behalf of a user (Admin only)")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/user/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, @RequestParam("userId") Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
