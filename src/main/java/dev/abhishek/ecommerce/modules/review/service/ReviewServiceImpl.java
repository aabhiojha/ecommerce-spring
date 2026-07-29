package dev.abhishek.ecommerce.modules.review.service;

import dev.abhishek.ecommerce.common.exceptions.ResourceNotFoundException;
import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.repository.OrderRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.entity.Review;
import dev.abhishek.ecommerce.modules.review.mapper.ReviewMapper;
import dev.abhishek.ecommerce.modules.review.repository.ReviewRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getAllReviewOfProduct(Long productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProduct_Id(productId, pageable);
        return new PagedResponse<>(reviewMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
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


    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User user = getUser();
        Review review = reviewRepository.findByIdAndUser(reviewId, user).orElseThrow(
                () -> new ResourceNotFoundException("The review doesn't exist"));

        reviewRepository.delete(review);
        log.info("The review has been deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewDto> getAllUserReviews(Pageable pageable) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<Review> page = reviewRepository.findByUser(user, pageable);
        return new PagedResponse<>(reviewMapper.toDtoList(page.getContent()), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() -> new UsernameNotFoundException("The user not found"));
        Review review = reviewRepository.findByIdAndUser(reviewId, user).orElseThrow(
                () -> new ResourceNotFoundException("The review doesn't exist"));

        reviewRepository.delete(review);
        log.info("The review has been deleted");

    }


    private User getUser() {
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        log.info("User : {} fetched", user.getUsername());
        return user;
    }
}
