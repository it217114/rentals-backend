package com.rentals.rentalbackend.applications;

import com.rentals.rentalbackend.applications.dto.*;
import com.rentals.rentalbackend.auth.*;
import com.rentals.rentalbackend.properties.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController @RequestMapping
@RequiredArgsConstructor
public class ApplicationController {
    private final RentalApplicationRepository apps;
    private final PropertyRepository props;
    private final UserRepository users;

    // TENANT δημιουργεί αίτηση
    @PostMapping("/applications") @PreAuthorize("hasRole('TENANT')")
    public void apply(@RequestBody @Valid ApplicationCreateReq r,
                      @AuthenticationPrincipal UserDetails principal) {
        var tenant = users.findByEmail(principal.getUsername()).orElseThrow();
        var prop = props.findById(r.propertyId()).orElseThrow();

        var a = new RentalApplication();
        a.setTenant(tenant);
        a.setProperty(prop);
        a.setMessage(r.message());
        apps.save(a);
    }

    // TENANT βλέπει τις αιτήσεις του
    @GetMapping("/applications/mine") @PreAuthorize("hasRole('TENANT')")
    public List<ApplicationRes> myApps(@AuthenticationPrincipal UserDetails principal) {
        var tenant = users.findByEmail(principal.getUsername()).orElseThrow();
        return apps.findByTenant(tenant).stream()
                .map(a -> new ApplicationRes(a.getId(), a.getProperty().getId(),
                        a.getTenant().getEmail(), a.getStatus().name(), a.getMessage()))
                .toList();
    }

    // OWNER βλέπει αιτήσεις για δικό του property
    @GetMapping("/owner/applications") @PreAuthorize("hasRole('OWNER')")
    public List<ApplicationRes> forOwner(@RequestParam Long propertyId,
                                         @AuthenticationPrincipal UserDetails principal) {
        var owner = users.findByEmail(principal.getUsername()).orElseThrow();
        var prop = props.findById(propertyId).orElseThrow();
        if (!prop.getOwner().getId().equals(owner.getId())) throw new RuntimeException("Not your property");
        return apps.findByProperty(prop).stream()
                .map(a -> new ApplicationRes(a.getId(), prop.getId(), a.getTenant().getEmail(),
                        a.getStatus().name(), a.getMessage()))
                .toList();
    }

    // OWNER approve/reject αίτηση
    @PostMapping("/owner/applications/{id}/approve") @PreAuthorize("hasRole('OWNER')")
    public void approve(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        updateStatus(id, principal, ApplicationStatus.APPROVED);
    }

    @PostMapping("/owner/applications/{id}/reject") @PreAuthorize("hasRole('OWNER')")
    public void reject(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        updateStatus(id, principal, ApplicationStatus.REJECTED);
    }

    private void updateStatus(Long id, UserDetails principal, ApplicationStatus st) {
        var owner = users.findByEmail(principal.getUsername()).orElseThrow();
        var a = apps.findById(id).orElseThrow();
        if (!a.getProperty().getOwner().getId().equals(owner.getId())) throw new RuntimeException("Not your property");
        a.setStatus(st);
        apps.save(a);
    }
}

