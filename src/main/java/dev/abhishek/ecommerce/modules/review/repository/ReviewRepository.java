package dev.abhishek.ecommerce.modules.review.repository;

import dev.abhishek.ecommerce.modules.review.dto.ReviewDto;
import dev.abhishek.ecommerce.modules.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_Id(Long productId);
}
