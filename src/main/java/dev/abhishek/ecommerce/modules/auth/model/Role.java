package dev.abhishek.ecommerce.modules.auth.model;

import dev.abhishek.ecommerce.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Roles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String name;

}
