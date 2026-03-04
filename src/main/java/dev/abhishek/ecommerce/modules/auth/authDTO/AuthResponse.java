package dev.abhishek.ecommerce.modules.auth.authDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private List<String> roles;

    public AuthResponse(String token, String username, List<String> roles) {
        this.token = token;
        this.username = username;
        this.roles = roles;
    }
}
