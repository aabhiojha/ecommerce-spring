package dev.abhishek.ecommerce.modules.order.repository;

import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
