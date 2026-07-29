package dev.abhishek.ecommerce.modules.user.service;

import dev.abhishek.ecommerce.modules.user.dtos.UpdateUserDto;
import dev.abhishek.ecommerce.modules.user.dtos.UserDto;

import dev.abhishek.ecommerce.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    PagedResponse<UserDto> getAllUsers(Pageable pageable);
    UserDto getCurrentUser();
    UserDto getUserById(Long userId);
    UserDto updateCurrentUser(UpdateUserDto updateUserDto);
    void deleteUser(Long userId);
}
