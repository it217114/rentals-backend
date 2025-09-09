package com.rentals.rentalbackend.properties;

import com.rentals.rentalbackend.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "properties")
@Getter @Setter @NoArgsConstructor
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false) private String title;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String city;
    private String address;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    private Integer bedrooms;
    private Integer bathrooms;
    private Double area;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status = ListingStatus.PENDING_APPROVAL;

    @Lob private String description;
    @Lob private String amenities;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images = new ArrayList<>();
}

