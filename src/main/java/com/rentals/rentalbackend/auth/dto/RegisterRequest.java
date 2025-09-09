package com.rentals.rentalbackend.auth.dto;

import com.rentals.rentalbackend.auth.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String fullName,
        @NotBlank String password,
        Set<Role> roles
) {}


