package dev.abhishek.ecommerce.modules.order.repository;

import dev.abhishek.ecommerce.modules.order.entity.Order;
import dev.abhishek.ecommerce.modules.order.misc.StatusChoice;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(User user);
    @Query("select o from Order o where o.user = :user order by o.created_at desc")
    Page<Order> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    Optional<Order> findByIdAndUser(UUID id, User user);

    List<Order> findByStatusAndUser(StatusChoice status, User user);

    Optional<Order> findByIdAndUserAndStatus(UUID id, User user, StatusChoice status);
}
