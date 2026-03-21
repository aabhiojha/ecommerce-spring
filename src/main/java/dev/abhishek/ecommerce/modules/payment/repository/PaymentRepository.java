package dev.abhishek.ecommerce.modules.payment.repository;

import dev.abhishek.ecommerce.modules.payment.entity.Payment;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByCheckoutSessionIdAndUser(String checkoutSessionId, User user);
    Optional<Payment> findByCheckoutSessionId(String checkoutSessionId);
}
