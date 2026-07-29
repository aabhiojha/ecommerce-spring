package dev.abhishek.ecommerce.modules.auth.repository;

import dev.abhishek.ecommerce.modules.auth.model.PasswordResetToken;
import dev.abhishek.ecommerce.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(Integer token);

    boolean existsByToken(Integer token);

    void deleteByUser(User user);
}
