package com.rentals.rentalbackend.security;

import com.rentals.rentalbackend.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u = repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("not found"));
        var auths = u.getRoles().stream()
                .map(r -> "ROLE_" + r.name())
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .toList();
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(), u.getPassword(), u.isEnabled(), true, true, true, auths);
    }
}

