package dev.abhishek.ecommerce.modules.auth.authDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetConfirmDTO {

    @NotNull
    private Integer token;

    @NotNull
    @Size(min = 8, max = 100)
    private String password;
}
