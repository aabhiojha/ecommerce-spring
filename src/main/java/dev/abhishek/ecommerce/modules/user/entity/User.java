package dev.abhishek.ecommerce.modules.user.entity;

import dev.abhishek.ecommerce.common.AuditableEntity;
import dev.abhishek.ecommerce.modules.auth.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotNull(message = "Username is required")
    @Column(name = "user_name", updatable = false, unique = true)
    private String userName;

    @NotNull
    private String password;

    @Email
    @NotNull(message = "Email is required")
    @Column(unique = true)
    private String email;

    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "userId", referencedColumnName = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleId", referencedColumnName = "roleId")}
    )
    private List<Role> roles = new ArrayList<>();


}
