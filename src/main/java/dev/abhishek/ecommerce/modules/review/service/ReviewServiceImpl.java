package dev.abhishek.ecommerce.modules.review.service;

import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.repository.OrderRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.entity.Review;
import dev.abhishek.ecommerce.modules.review.mapper.ReviewMapperImpl;
import dev.abhishek.ecommerce.modules.review.repository.ReviewRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapperImpl reviewMapper;
    private final OrderRepository orderRepository;

    @Override
    public List<ReviewDto> getAllReviewOfProduct(Long productId) {
        List<Review> byProductId = reviewRepository.findByProduct_Id(productId);
        return reviewMapper.toDtoList(byProductId);
    }

    @Override
    @Transactional
    public ReviewDto createReview(CreateReviewDto createReviewDto) {
        User user = getUser();
        Order order = orderRepository.findByIdAndUserAndStatus(createReviewDto.getOrderId(), user, StatusChoice.DELIVERED)
                .orElseThrow(() -> new IllegalArgumentException("Order not found or not delivered."));

        if (reviewRepository.existsByUserAndProduct_Id(user, createReviewDto.getProductId())) {
            throw new IllegalArgumentException("You have already reviewed this product.");
        }

        Product product = order.getOrderItems().stream()
                .map(OrderItem::getProduct)
                .filter(itemProduct -> itemProduct.getId().equals(createReviewDto.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The product is not part of this order."));

        Review build = Review.builder()
                .product(product)
                .user(user)
                .reviewMessage(createReviewDto.getReviewMessage())
                .rating(createReviewDto.getRating())
                .build();
        return reviewMapper.toDto(reviewRepository.save(build));
    }

    private User getUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        log.info("User : {} fetched", user.getUsername());
        return user;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }
}
