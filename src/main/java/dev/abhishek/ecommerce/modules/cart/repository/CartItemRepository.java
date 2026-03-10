package dev.abhishek.ecommerce.modules.cart.repository;

import dev.abhishek.ecommerce.modules.cart.entity.CartItem;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndCart_User(Long id, User cartUser);
    List<CartItem> findAllByCart_User(User cartUser);
}
