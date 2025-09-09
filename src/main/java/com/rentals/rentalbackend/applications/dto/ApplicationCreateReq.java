package com.rentals.rentalbackend.applications.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationCreateReq(
        @NotNull Long propertyId,
        String message
) {}
