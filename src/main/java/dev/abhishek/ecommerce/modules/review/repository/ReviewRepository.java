package dev.abhishek.ecommerce.modules.review.repository;

import dev.abhishek.ecommerce.modules.review.entity.Review;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct_Id(Long productId, Pageable pageable);
    boolean existsByUserAndProduct_Id(User user, Long productId);

    Optional<Review> findByIdAndUser(Long id, User user);

    Page<Review> findByUser(User user, Pageable pageable);
}
