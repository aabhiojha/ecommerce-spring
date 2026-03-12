package dev.abhishek.ecommerce.modules.user.controller;

import dev.abhishek.ecommerce.modules.user.dtos.UpdateUserDto;
import dev.abhishek.ecommerce.modules.user.dtos.UserDto;
import dev.abhishek.ecommerce.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.debug("Received request to fetch all users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        log.debug("Received request to fetch current user profile");
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.debug("Received request to fetch user by id: {}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UpdateUserDto updateUserDto) {
        log.debug("Received request to update current user profile");
        return ResponseEntity.ok(userService.updateCurrentUser(updateUserDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.debug("Received request to delete user by id: {}", userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
