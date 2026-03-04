package dev.abhishek.ecommerce.modules.user.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class User {
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;
}
