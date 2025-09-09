package com.rentals.rentalbackend.properties;

import com.rentals.rentalbackend.auth.UserRepository;
import com.rentals.rentalbackend.properties.dto.PropertyCreateReq;
import com.rentals.rentalbackend.properties.dto.PropertyRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static com.rentals.rentalbackend.properties.PropertySpecifications.*;

@RestController @RequestMapping("/properties") @RequiredArgsConstructor
public class PropertyController {
    private final PropertyRepository repo;
    private final UserRepository users;

    // Δημόσια αναζήτηση (επιτρέπεται χωρίς login)
    @GetMapping
    public Page<PropertyRes> search(@RequestParam(required = false) String city,
                                    @RequestParam(required = false) BigDecimal minPrice,
                                    @RequestParam(required = false) BigDecimal maxPrice,
                                    @RequestParam(required = false) Integer bedrooms,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "12") int size,
                                    @RequestParam(defaultValue = "id,desc") String sort) {
        Sort s = sort.toLowerCase().contains("asc")
                ? Sort.by(sort.split(",")[0]).ascending()
                : Sort.by(sort.split(",")[0]).descending();
        var pageable = PageRequest.of(page, size, s);

        Specification<Property> spec = Specification
                .where(statusEquals(ListingStatus.APPROVED))
                .and(cityEquals(city))
                .and(minPrice(minPrice))
                .and(maxPrice(maxPrice))
                .and(bedroomsAtLeast(bedrooms));

        return repo.findAll(spec, pageable).map(p -> new PropertyRes(
                p.getId(), p.getTitle(), p.getType(), p.getCity(), p.getAddress(), p.getPrice(),
                p.getBedrooms(), p.getBathrooms(), p.getArea(), p.getStatus()
        ));
    }

    // Δημιουργία από OWNER (μπαίνει PENDING_APPROVAL)
    @PostMapping @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> create(@RequestBody @Valid PropertyCreateReq r,
                                    @AuthenticationPrincipal UserDetails principal) {
        var owner = users.findByEmail(principal.getUsername()).orElseThrow();

        var p = new Property();
        p.setOwner(owner);
        p.setTitle(r.title());
        p.setType(r.type());
        p.setCity(r.city());
        p.setAddress(r.address());
        p.setPrice(r.price());
        p.setBedrooms(r.bedrooms());
        p.setBathrooms(r.bathrooms());
        p.setArea(r.area());
        p.setDescription(r.description());
        p.setAmenities(r.amenities());
        p.setStatus(ListingStatus.PENDING_APPROVAL);

        repo.save(p);
        return ResponseEntity.ok().build();
    }

    // Τα δικά μου ακίνητα (OWNER)
    @GetMapping("/mine") @PreAuthorize("hasRole('OWNER')")
    public Page<PropertyRes> mine(@AuthenticationPrincipal UserDetails principal,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        var owner = users.findByEmail(principal.getUsername()).orElseThrow();
        var pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Specification<Property> spec = (root, q, cb) -> cb.equal(root.get("owner"), owner);
        return repo.findAll(spec, pageable).map(p -> new PropertyRes(
                p.getId(), p.getTitle(), p.getType(), p.getCity(), p.getAddress(), p.getPrice(),
                p.getBedrooms(), p.getBathrooms(), p.getArea(), p.getStatus()
        ));
    }
}

