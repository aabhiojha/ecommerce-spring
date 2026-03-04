package dev.abhishek.ecommerce.modules.user.service;

import dev.abhishek.ecommerce.modules.auth.RoleRepository;
import dev.abhishek.ecommerce.modules.auth.model.Role;
import dev.abhishek.ecommerce.modules.user.entity.User;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void createUser(User user) {
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            if (roleRepository.findByNameIgnoreCase(role.getName()) == null) {
                roleRepository.save(role);
            }
            userRepository.save(user);
        }
    }


}
