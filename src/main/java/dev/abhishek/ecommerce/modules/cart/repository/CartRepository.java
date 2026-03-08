package dev.abhishek.ecommerce.modules.cart.repository;

import dev.abhishek.ecommerce.modules.cart.entity.Cart;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
