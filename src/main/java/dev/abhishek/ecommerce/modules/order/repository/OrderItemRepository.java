package dev.abhishek.ecommerce.modules.order.repository;

import dev.abhishek.ecommerce.modules.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
