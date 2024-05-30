package com.example.fluere.userentity.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Document
@EqualsAndHashCode(callSuper = false)
public class AppUser implements UserDetails {

    @MongoId
    private final UserIdWrapper userId;
    private final String login;
    private final String email;
    private final String password;
    private final Role role;
    private final Boolean enabled;
    private final LocalDateTime creationTime;

    public static AppUser newOf(UserIdWrapper userId,
                                String login,
                                String email,
                                String password,
                                Role role,
                                Boolean enabled,
                                LocalDateTime creationTime
    ) {
        return new AppUser(
                userId,
                login,
                email,
                password,
                role,
                enabled,
                creationTime
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId.id().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
