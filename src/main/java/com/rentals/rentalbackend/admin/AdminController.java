package com.rentals.rentalbackend.admin;

import com.rentals.rentalbackend.properties.ListingStatus;
import com.rentals.rentalbackend.properties.Property;
import com.rentals.rentalbackend.properties.PropertyRepository;
import com.rentals.rentalbackend.properties.dto.PropertyRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PropertyRepository props;

    /** Λίστα εκκρεμών προς έγκριση */
    @GetMapping("/properties/pending")
    public Page<PropertyRes> pending(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size,
                                     @RequestParam(defaultValue = "id,desc") String sort) {

        Sort s = sort.toLowerCase().contains("asc")
                ? Sort.by(sort.split(",")[0]).ascending()
                : Sort.by(sort.split(",")[0]).descending();
        Pageable pageable = PageRequest.of(page, size, s);

        Specification<Property> spec = (root, q, cb) ->
                cb.equal(root.get("status"), ListingStatus.PENDING_APPROVAL);

        return props.findAll(spec, pageable)
                .map(p -> new PropertyRes(
                        p.getId(), p.getTitle(), p.getType(), p.getCity(), p.getAddress(),
                        p.getPrice(), p.getBedrooms(), p.getBathrooms(), p.getArea(), p.getStatus()
                ));
    }

    /** Έγκριση αγγελίας */
    @PostMapping("/properties/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        var p = props.findById(id).orElseThrow();
        p.setStatus(ListingStatus.APPROVED);
        props.save(p);
        return ResponseEntity.ok().build();
    }

    /** Απόρριψη αγγελίας */
    @PostMapping("/properties/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        var p = props.findById(id).orElseThrow();
        p.setStatus(ListingStatus.REJECTED);
        props.save(p);
        return ResponseEntity.ok().build();
    }
}
