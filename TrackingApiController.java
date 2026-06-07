package webit.Poject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import webit.Poject.dto.*;
import webit.Poject.model.Jetski;
import webit.Poject.model.Location;
import webit.Poject.model.Penalty;
import webit.Poject.model.Rental;
import webit.Poject.repository.JetskiRepository;
import webit.Poject.repository.LocationRepository;
import webit.Poject.repository.PenaltyRepository;
import webit.Poject.repository.RentalRepository;
import webit.Poject.security.UserPrincipal;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrackingApiController {

    public static final double VAT_RATE = 0.18;
    public static final int DEFAULT_RENTAL_MINUTES = 15;

    private final LocationRepository locationRepository;
    private final JetskiRepository jetskiRepository;
    private final RentalRepository rentalRepository;
    private final PenaltyRepository penaltyRepository;

    private static final String SEA_HERO =
            "https://images.unsplash.com/photo-1505142468610-359e7d316be0?auto=format&fit=crop&q=80&w=1200";

    private static boolean hasRole(UserPrincipal p, String role) {
        return p.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    @GetMapping("/locations")
    public List<LocationResponse> listLocations() {
        return locationRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Location::getSortOrder))
                .map(LocationResponse::from)
                .toList();
    }

    @GetMapping("/jetskis")
    public List<JetskiResponse> jetskisByLocation(@RequestParam String locationId) {
        return jetskiRepository.findByLocationIdOrderByIdAsc(locationId).stream()
                .map(JetskiResponse::from)
                .toList();
    }

    @GetMapping("/jetskis/owner/{ownerId}")
    public List<JetskiResponse> jetskisForOwner(@PathVariable String ownerId) {
        return jetskiRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(JetskiResponse::from)
                .toList();
    }

    @PostMapping("/jetskis")
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public ResponseEntity<JetskiResponse> createJetski(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateJetskiRequest req
    ) {
        var user = principal.getUser();
        String ownerId = String.valueOf(user.getUserId());
        locationRepository.findById(req.locationId()).orElseThrow();

        Jetski j = Jetski.builder()
                .model(req.model())
                .ownerId(ownerId)
                .ownerName(user.getFullName())
                .locationId(req.locationId())
                .status("available")
                .priceUsd15min(req.priceUsd15min())
                .priceTzs15min(req.priceTzs15min())
                .imageUrl(req.imageUrl() != null && !req.imageUrl().isBlank()
                        ? req.imageUrl()
                        : "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&q=80&w=800")
                .boundaryLat(-6.1659)
                .boundaryLng(39.2026)
                .boundaryRadiusKm(2.0)
                .build();
        j = jetskiRepository.save(j);
        return ResponseEntity.ok(JetskiResponse.from(j));
    }

    @DeleteMapping("/jetskis/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public ResponseEntity<?> deleteJetski(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        Jetski j = jetskiRepository.findById(Long.parseLong(id)).orElseThrow();
        if (!j.getOwnerId().equals(String.valueOf(principal.getUser().getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not your jet ski"));
        }
        jetskiRepository.delete(j);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/jetskis/{id}/status")
    @PreAuthorize("hasRole('OWNER')")
    @Transactional
    public ResponseEntity<?> setJetskiStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "status required"));
        }
        Jetski j = jetskiRepository.findById(Long.parseLong(id)).orElseThrow();
        if (!j.getOwnerId().equals(String.valueOf(principal.getUser().getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not your jet ski"));
        }
        j.setStatus(status);
        jetskiRepository.save(j);
        return ResponseEntity.ok(JetskiResponse.from(j));
    }

    @GetMapping("/rentals/user/{userId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OWNER') or hasRole('ADMIN') or hasRole('GOVERNMENT')")
    public List<RentalResponse> rentalsForUser(@PathVariable String userId, @AuthenticationPrincipal UserPrincipal p) {
        enforceUserScope(userId, p);
        return rentalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(RentalResponse::from)
                .toList();
    }

    @GetMapping("/rentals/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('GOVERNMENT')")
    public List<RentalResponse> rentalsForOwner(@PathVariable String ownerId, @AuthenticationPrincipal UserPrincipal p) {
        if (hasRole(p, "OWNER") && !ownerId.equals(String.valueOf(p.getUser().getUserId()))) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return rentalRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(RentalResponse::from)
                .toList();
    }

    @GetMapping("/rentals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GOVERNMENT')")
    public List<RentalResponse> allRentals() {
        return rentalRepository.findAll().stream()
                .sorted(Comparator.comparing(Rental::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(200)
                .map(RentalResponse::from)
                .toList();
    }

    @GetMapping("/rentals/{id}")
    public RentalResponse getRental(@PathVariable String id) {
        Rental r = rentalRepository.findById(Long.parseLong(id)).orElseThrow();
        return RentalResponse.from(r);
    }

    @PostMapping("/rentals")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ResponseEntity<RentalResponse> createRental(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateRentalRequest req
    ) {
        Jetski jetski = jetskiRepository.findById(Long.parseLong(req.jetskiId())).orElseThrow();
        if (!"available".equals(jetski.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Location loc = locationRepository.findById(req.locationId()).orElseThrow();
        var user = principal.getUser();
        String userName = user.getUsername();
        String userPhone = user.getPhoneNumber() != null ? user.getPhoneNumber() : "";

        int duration = req.durationMinutes();
        double subtotal = req.currency().equals("USD")
                ? jetski.getPriceUsd15min() * (duration / (double) DEFAULT_RENTAL_MINUTES)
                : jetski.getPriceTzs15min() * (duration / (double) DEFAULT_RENTAL_MINUTES);
        double vat = subtotal * VAT_RATE;
        double total = subtotal + vat;

        Instant now = Instant.now();
        Instant end = now.plus(duration, ChronoUnit.MINUTES);

        Rental rental = Rental.builder()
                .userId(String.valueOf(user.getUserId()))
                .userName(userName)
                .userPhone(userPhone)
                .jetskiId(String.valueOf(jetski.getId()))
                .jetskiModel(jetski.getModel())
                .ownerId(jetski.getOwnerId())
                .ownerName(jetski.getOwnerName())
                .locationId(loc.getId())
                .locationName(loc.getName())
                .durationMinutes(duration)
                .amountSubtotal(subtotal)
                .vatAmount(vat)
                .amountTotal(total)
                .currency(req.currency())
                .paymentMethod(req.paymentMethod())
                .paymentStatus("active")
                .startTime(now)
                .endTime(end)
                .createdAt(now)
                .build();
        rental = rentalRepository.save(rental);

        jetski.setStatus("rented");
        jetskiRepository.save(jetski);

        return ResponseEntity.ok(RentalResponse.from(rental));
    }

    @PatchMapping("/rentals/{id}/finish")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ResponseEntity<RentalResponse> finishRental(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String id
    ) {
        Rental rental = rentalRepository.findById(Long.parseLong(id)).orElseThrow();
        if (!rental.getUserId().equals(String.valueOf(principal.getUser().getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        rental.setPaymentStatus("finished");
        rentalRepository.save(rental);

        Jetski j = jetskiRepository.findById(Long.parseLong(rental.getJetskiId())).orElseThrow();
        j.setStatus("available");
        jetskiRepository.save(j);

        return ResponseEntity.ok(RentalResponse.from(rental));
    }

    @GetMapping("/penalties/user/{userId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN') or hasRole('GOVERNMENT')")
    public List<PenaltyResponse> penaltiesForUser(@PathVariable String userId, @AuthenticationPrincipal UserPrincipal p) {
        enforceUserScope(userId, p);
        return penaltyRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    @GetMapping("/penalties/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') or hasRole('GOVERNMENT')")
    public List<PenaltyResponse> penaltiesForOwner(@PathVariable String ownerId, @AuthenticationPrincipal UserPrincipal p) {
        if (hasRole(p, "OWNER") && !ownerId.equals(String.valueOf(p.getUser().getUserId()))) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
        return penaltyRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(PenaltyResponse::from)
                .toList();
    }

    @GetMapping("/penalties")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GOVERNMENT')")
    public List<PenaltyResponse> allPenalties() {
        return penaltyRepository.findAll().stream()
                .sorted(Comparator.comparing(Penalty::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(200)
                .map(PenaltyResponse::from)
                .toList();
    }

    @PostMapping("/penalties")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ResponseEntity<PenaltyResponse> createPenalty(@RequestBody Map<String, Object> body) {
        Penalty p = new Penalty();
        p.setRentalId(String.valueOf(body.get("rentalId")));
        p.setUserId(String.valueOf(body.get("userId")));
        p.setUserName((String) body.get("userName"));
        p.setJetskiModel((String) body.get("jetskiModel"));
        p.setOwnerId(String.valueOf(body.get("ownerId")));
        p.setMinutesExceeded(((Number) body.get("minutesExceeded")).intValue());
        p.setAmount(((Number) body.get("amount")).doubleValue());
        p.setStatus(body.getOrDefault("status", "pending").toString());
        p.setCreatedAt(Instant.now());
        p = penaltyRepository.save(p);
        String rentalIdStr = p.getRentalId();
        rentalRepository.findById(Long.parseLong(rentalIdStr)).ifPresent(r -> {
            r.setPaymentStatus("penalty");
            rentalRepository.save(r);
        });
        return ResponseEntity.ok(PenaltyResponse.from(p));
    }

    private void enforceUserScope(String userId, UserPrincipal p) {
        boolean elevated = hasRole(p, "ADMIN") || hasRole(p, "GOVERNMENT");
        if (!elevated && !userId.equals(String.valueOf(p.getUser().getUserId()))) {
            throw new org.springframework.security.access.AccessDeniedException("Forbidden");
        }
    }

    /** Used by admin seed — same payload structure as Flutter legacy seed. */
    public static List<Map<String, Object>> defaultZones() {
        return List.of(
                Map.of("id", "paje", "name", "Paje", "sortOrder", 1),
                Map.of("id", "kiwengwa", "name", "Kiwengwa", "sortOrder", 2),
                Map.of("id", "nungwi", "name", "Nungwi", "sortOrder", 3)
        );
    }
}
