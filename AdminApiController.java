package webit.Poject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import webit.Poject.dto.OwnerRequestAdminResponse;
import webit.Poject.dto.UserResponse;
import webit.Poject.model.Location;
import webit.Poject.model.OwnerRequest;
import webit.Poject.model.enums.OwnerRequestStatus;
import webit.Poject.model.enums.Role;
import webit.Poject.repository.LocationRepository;
import webit.Poject.repository.OwnerRequestRepository;
import webit.Poject.repository.UserRepository;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiController {

    private static final String SEA_HERO =
            "https://images.unsplash.com/photo-1505142468610-359e7d316be0?auto=format&fit=crop&q=80&w=1200";

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final OwnerRequestRepository ownerRequestRepository;

    @PostMapping("/seed/locations")
    @Transactional
    public ResponseEntity<Map<String, String>> seedLocations() {
        for (Map<String, Object> z : webit.Poject.controller.TrackingApiController.defaultZones()) {
            Location loc = Location.builder()
                    .id((String) z.get("id"))
                    .name((String) z.get("name"))
                    .sortOrder(((Number) z.get("sortOrder")).intValue())
                    .imageUrl(SEA_HERO)
                    .build();
            locationRepository.save(loc);
        }
        return ResponseEntity.ok(Map.of("message", "Beach zones saved"));
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .limit(100)
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/owner-requests")
    public List<OwnerRequestAdminResponse> listOwnerRequests() {
        return ownerRequestRepository.findAllJoinUserOrderByCreatedAtDesc().stream()
                .map(OwnerRequestAdminResponse::from)
                .toList();
    }

    @PostMapping("/owner-requests/{id}/approve")
    @Transactional
    public ResponseEntity<?> approveOwnerRequest(@PathVariable Long id) {
        OwnerRequest r = ownerRequestRepository.findById(id).orElseThrow();
        if (r.getRequestStatus() != OwnerRequestStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Request is not pending"));
        }
        var user = r.getUser();
        user.setRole(Role.OWNER);
        user.setRequestedRole(null);
        userRepository.save(user);
        r.setRequestStatus(OwnerRequestStatus.APPROVED);
        ownerRequestRepository.save(r);
        return ResponseEntity.ok(Map.of("message", "Owner approved"));
    }

    @PostMapping("/owner-requests/{id}/reject")
    @Transactional
    public ResponseEntity<?> rejectOwnerRequest(@PathVariable Long id) {
        OwnerRequest r = ownerRequestRepository.findById(id).orElseThrow();
        if (r.getRequestStatus() != OwnerRequestStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Request is not pending"));
        }
        var user = r.getUser();
        user.setRequestedRole(null);
        userRepository.save(user);
        r.setRequestStatus(OwnerRequestStatus.REJECTED);
        ownerRequestRepository.save(r);
        return ResponseEntity.ok(Map.of("message", "Owner request rejected"));
    }
}
