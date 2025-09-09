package com.rentals.rentalbackend.applications;

import com.rentals.rentalbackend.auth.User;
import com.rentals.rentalbackend.properties.Property;
import jakarta.persistence.*;
import lombok.Getter; import lombok.Setter; import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity @Table(name = "rental_applications")
@Getter @Setter @NoArgsConstructor
public class RentalApplication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "property_id") private Property property;
    @ManyToOne(optional = false) @JoinColumn(name = "tenant_id")   private User tenant;

    @Enumerated(EnumType.STRING) @Column(nullable = false) private ApplicationStatus status = ApplicationStatus.PENDING;

    @Lob private String message;
    @Column(name = "created_at") private Instant createdAt = Instant.now();
}

