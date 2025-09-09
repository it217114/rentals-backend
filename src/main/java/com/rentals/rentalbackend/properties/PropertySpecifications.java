package com.rentals.rentalbackend.properties;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PropertySpecifications {
    public static Specification<Property> cityEquals(String city) {
        return (root, q, cb) -> (city == null || city.isBlank())
                ? null
                : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }
    public static Specification<Property> statusEquals(ListingStatus st) {
        return (root, q, cb) -> (st == null) ? null : cb.equal(root.get("status"), st);
    }
    public static Specification<Property> minPrice(BigDecimal p) {
        return (root, q, cb) -> (p == null) ? null : cb.ge(root.get("price"), p);
    }
    public static Specification<Property> maxPrice(BigDecimal p) {
        return (root, q, cb) -> (p == null) ? null : cb.le(root.get("price"), p);
    }
    public static Specification<Property> bedroomsAtLeast(Integer b) {
        return (root, q, cb) -> (b == null) ? null : cb.ge(root.get("bedrooms"), b);
    }
}

