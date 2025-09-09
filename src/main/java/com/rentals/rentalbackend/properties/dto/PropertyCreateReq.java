package com.rentals.rentalbackend.properties.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record PropertyCreateReq(
        @NotBlank String title,
        @NotBlank String type,
        @NotBlank String city,
        String address,
        @NotNull BigDecimal price,
        Integer bedrooms,
        Integer bathrooms,
        Double area,
        String description,
        String amenities,
        List<String> imageUrls
) {}


