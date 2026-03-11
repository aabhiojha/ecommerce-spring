package dev.abhishek.ecommerce.modules.review.repository;

import dev.abhishek.ecommerce.modules.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
