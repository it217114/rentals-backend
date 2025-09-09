package com.rentals.rentalbackend.auth;

import com.rentals.rentalbackend.auth.dto.*;
import com.rentals.rentalbackend.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;

@RestController @RequestMapping("/auth") @RequiredArgsConstructor
public class AuthController {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest r) {
        var u = new User();
        u.setEmail(r.email());
        u.setFullName(r.fullName());
        u.setPassword(encoder.encode(r.password()));
        u.setRoles((r.roles()==null || r.roles().isEmpty()) ? Set.of(Role.TENANT) : r.roles());
        repo.save(u);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest r) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password()));
        var u = repo.findByEmail(r.email()).orElseThrow();
        var token = jwt.generate(u.getEmail(), Map.of("roles", u.getRoles()));
        return new AuthResponse(token);
    }
}

