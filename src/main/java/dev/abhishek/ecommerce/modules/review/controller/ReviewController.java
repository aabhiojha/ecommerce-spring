package dev.abhishek.ecommerce.modules.review.controller;

import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.mapper.ReviewMapperImpl;
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

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("{productId}")
    public ResponseEntity<List<ReviewDto>> getAllReviews(@PathVariable Long productId){
        List<ReviewDto> reviews = reviewService.getAllReviewOfProduct(productId);
        if (reviews == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(reviews);
    }


    @PostMapping
    public ResponseEntity<ReviewDto> getAllReviews(@RequestBody CreateReviewDto createReviewDto) throws Exception {
        ReviewDto review = reviewService.createReview(createReviewDto);
        return ResponseEntity.ok(review);
    }



}
