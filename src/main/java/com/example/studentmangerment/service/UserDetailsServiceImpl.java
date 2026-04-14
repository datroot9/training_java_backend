package com.example.studentmangerment.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.studentmangerment.dao.UserDao;
import com.example.studentmangerment.entity.User;

import lombok.AllArgsConstructor;

/**
 * Spring Security {@link UserDetailsService} that loads users from the database for JWT authentication.
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    /** Loads {@link User} rows by username. */
    private final UserDao userDao;

    /**
     * Builds a {@link UserDetails} instance with password and {@code ROLE_*} authorities.
     *
     * @param username login name from the JWT subject
     * @return authenticated user details
     * @throws UsernameNotFoundException if no user exists for the username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        String role = user.getRole() == null || user.getRole().isBlank() ? "USER" : user.getRole();
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .build();
    }
}
