package dev.abhishek.ecommerce.modules.user.service;

import dev.abhishek.ecommerce.modules.user.dtos.UpdateUserDto;
import dev.abhishek.ecommerce.modules.user.dtos.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getCurrentUser();
    UserDto getUserById(Long userId);
    UserDto updateCurrentUser(UpdateUserDto updateUserDto);
    void deleteUser(Long userId);
}
