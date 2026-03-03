package dev.abhishek.ecommerce.modules.auth;

import dev.abhishek.ecommerce.modules.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByNameIgnoreCase(String name);
}
