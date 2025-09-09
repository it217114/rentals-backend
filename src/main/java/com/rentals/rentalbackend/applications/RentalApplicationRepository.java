package com.rentals.rentalbackend.applications;

import com.rentals.rentalbackend.auth.User;
import com.rentals.rentalbackend.properties.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RentalApplicationRepository extends JpaRepository<RentalApplication, Long> {
    List<RentalApplication> findByTenant(User tenant);
    List<RentalApplication> findByProperty(Property property);
}

