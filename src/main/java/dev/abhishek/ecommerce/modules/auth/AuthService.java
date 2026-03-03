package dev.abhishek.ecommerce.modules.auth;

import dev.abhishek.ecommerce.modules.auth.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;

    public List<Role> getRoleFromRepo(String role){
        return roleRepository.findByNameIgnoreCase(role);
    }
}
