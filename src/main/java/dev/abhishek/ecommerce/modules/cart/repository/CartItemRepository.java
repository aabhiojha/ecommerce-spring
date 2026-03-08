package dev.abhishek.ecommerce.modules.cart.repository;

import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
