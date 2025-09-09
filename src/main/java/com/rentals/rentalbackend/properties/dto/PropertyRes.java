package com.rentals.rentalbackend.properties.dto;

import com.rentals.rentalbackend.properties.ListingStatus;
import java.math.BigDecimal;

public record PropertyRes(
        Long id, String title, String type, String city, String address,
        BigDecimal price, Integer bedrooms, Integer bathrooms, Double area,
        ListingStatus status
) {}

