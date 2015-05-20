package com.teamd.taxi.authentication;

import com.teamd.taxi.entity.Driver;
import com.teamd.taxi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AuthenticatedUser implements UserDetails {

    private String password;
    private String email;
    private Collection<SimpleGrantedAuthority> authorities;
    private boolean enabled;
    private Long id;

    public AuthenticatedUser(User user) {
        email = user.getEmail();
        password = user.getUserPassword();
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getUserRole().name()));
        enabled = user.isConfirmed();
        id = user.getId();
    }

    public AuthenticatedUser(Driver driver) {
        email = driver.getEmail();
        password = driver.getPassword();
        authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
        enabled = driver.isEnabled();
        id = Long.valueOf(driver.getId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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

    public long getId() {
        return id;
    }
}
