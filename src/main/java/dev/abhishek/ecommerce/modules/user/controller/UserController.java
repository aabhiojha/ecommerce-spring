package dev.abhishek.ecommerce.modules.user.controller;

import dev.abhishek.ecommerce.modules.user.model.User;
import dev.abhishek.ecommerce.modules.user.repository.UserRepository;
import dev.abhishek.ecommerce.modules.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/create")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}
