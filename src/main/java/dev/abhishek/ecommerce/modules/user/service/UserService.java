package dev.abhishek.ecommerce.modules.user.service;

import dev.abhishek.ecommerce.modules.auth.RoleRepository;
import dev.abhishek.ecommerce.modules.auth.model.Role;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;



}
