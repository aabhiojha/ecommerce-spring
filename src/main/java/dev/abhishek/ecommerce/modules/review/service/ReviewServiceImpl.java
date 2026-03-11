package dev.abhishek.ecommerce.modules.review.service;

import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.order.repository.OrderItemRepository;
import dev.abhishek.ecommerce.modules.order.repository.OrderRepository;
import dev.abhishek.ecommerce.modules.product.entity.Product;
import dev.abhishek.ecommerce.modules.product.repository.ProductRepository;
import dev.abhishek.ecommerce.modules.review.dto.CreateReviewDto;
import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.entity.Review;
import dev.abhishek.ecommerce.modules.review.mapper.ReviewMapper;
import dev.abhishek.ecommerce.modules.review.mapper.ReviewMapperImpl;
import dev.abhishek.ecommerce.modules.review.repository.ReviewRepository;
import dev.abhishek.ecommerce.modules.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapperImpl reviewMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<ReviewDto> getAllReviewOfProduct(Long productId) {
        List<Review> byProductId = reviewRepository.findByProduct_Id(productId);
        return reviewMapper.toDtoList(byProductId);
    }

    @Override
    @Transactional
    public ReviewDto createReview(CreateReviewDto createReviewDto) {
        // laying some fucking ground rules here
        // only users that have ordered the product and have the order status as delivered
        // are allowed to leave review

        User user = getUser();
        // check if order has the product that we wish to leave review for

//        Product product = productRepository.findById(createReviewDto.getProductId())
//                .orElseThrow(() -> new RuntimeException("The product not found"));
//        log.debug("Product with id: {} dooes not exist", createReviewDto.getProductId());
//
        // getting the order
        Optional<Order> order = orderRepository.findByIdAndUserAndStatus(createReviewDto.getOrderId(), user, StatusChoice.DELIVERED);

        if (order.isEmpty()) {
            log.debug("Either it's not your order or not its not delivered yet.");
            return null;
        }

        // check if the product is in the order

        Product product = null;

        for (OrderItem item : order.get().getOrderItems()) {
            if (item.getProduct().getId() == createReviewDto.getProductId()) {
                product = item.getProduct();
                break;
            }
        }

        if (product == null) {
            log.debug("The product is not in order list");
            return null;

        }
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
