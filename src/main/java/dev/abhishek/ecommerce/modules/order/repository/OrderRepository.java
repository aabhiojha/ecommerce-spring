package dev.abhishek.ecommerce.modules.order.repository;

import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(User user);
    @Query("select o from Order o where o.user = :user order by o.created_at desc")
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);
    Optional<Order> findByIdAndUser(UUID id, User user);
}
