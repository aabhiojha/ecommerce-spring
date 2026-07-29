package dev.abhishek.ecommerce.modules.auth.authDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {

    @NotBlank
    @Email
    private String email;
}
