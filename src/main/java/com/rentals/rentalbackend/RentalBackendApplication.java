package com.rentals.rentalbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class RentalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalBackendApplication.class, args);
    }

    // ΒΗΜΑ 10: seed admin user μία φορά στην εκκίνηση
    @Bean
    CommandLineRunner seedAdmin(
            com.rentals.rentalbackend.auth.UserRepository repo,
            org.springframework.security.crypto.password.PasswordEncoder encoder) {

        return args -> repo.findByEmail("admin@rentals.local").orElseGet(() -> {
            var u = new com.rentals.rentalbackend.auth.User();
            u.setEmail("admin@rentals.local");
            u.setFullName("Platform Admin");
            u.setPassword(encoder.encode("admin123"));
            u.setRoles(java.util.Set.of(com.rentals.rentalbackend.auth.Role.ADMIN));
            return repo.save(u);
        });
    }
}
