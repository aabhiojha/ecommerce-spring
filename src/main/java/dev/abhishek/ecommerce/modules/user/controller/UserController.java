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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "Users", description = "Endpoints for user profile and management")
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieves a list of all registered users (Admin only)")
    @GetMapping
    public ResponseEntity<PagedResponse<UserDto>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") int pageNo,
            @RequestParam(required = false, defaultValue = "20") int pageSize
    ) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(pageNo, pageSize)));
    }

    @Operation(summary = "Get current user", description = "Retrieves the profile of the currently authenticated user")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        log.debug("Received request to fetch current user profile");
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieves a user's profile by their ID (Admin only)")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        log.debug("Received request to fetch user by id: {}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(summary = "Update current user", description = "Updates the profile of the currently authenticated user")
    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(@RequestBody UpdateUserDto updateUserDto) {
        log.debug("Received request to update current user profile");
        return ResponseEntity.ok(userService.updateCurrentUser(updateUserDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes a user account by their ID (Admin only)")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.debug("Received request to delete user by id: {}", userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
