package com.rentals.rentalbackend.properties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "property_images")
@Getter @Setter @NoArgsConstructor
public class PropertyImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "property_id")
    private Property property;

    @Column(nullable = false, length = 500)
    private String url;
}

