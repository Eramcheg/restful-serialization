package com.softserve.itacademy.security;

import com.softserve.itacademy.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private long id;

    private String firstName;
    private String username;
    private String password;
    private boolean isFunctional;
    private Collection<? extends GrantedAuthority> roles;


    public long getId() {return id;}

    public String getFirstName() {return firstName;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isFunctional;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isFunctional;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isFunctional;
    }

    @Override
    public boolean isEnabled() {
        return isFunctional;
    }


    public static UserDetails getUserDetails(User user) {
        return new CustomUserDetails(user.getId(),
                user.getFirstName(),
                user.getUsername(),
                user.getPassword(),
                true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())));
    }
}
