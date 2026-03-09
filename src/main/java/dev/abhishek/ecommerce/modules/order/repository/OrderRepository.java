package dev.abhishek.ecommerce.modules.order.repository;

import dev.abhishek.ecommerce.modules.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
