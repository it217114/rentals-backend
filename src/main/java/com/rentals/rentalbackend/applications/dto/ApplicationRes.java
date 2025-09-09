package com.rentals.rentalbackend.applications.dto;

public record ApplicationRes(
        Long id,
        Long propertyId,
        String tenantEmail,
        String status,
        String message
) {}

