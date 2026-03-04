package dev.abhishek.ecommerce.modules.auth.authDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RegisterRequest {
    private String username;
    private String password;
}
