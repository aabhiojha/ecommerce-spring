package dev.abhishek.ecommerce.modules.auth.authDTO;

import kotlin.internal.AccessibleLateinitPropertyLiteral;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private List<String> roles;
}
