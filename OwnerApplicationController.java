package webit.Poject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import webit.Poject.dto.OwnerApplicationRequest;
import webit.Poject.model.OwnerRequest;
import webit.Poject.model.enums.OwnerRequestStatus;
import webit.Poject.model.enums.Role;
import webit.Poject.repository.OwnerRequestRepository;
import webit.Poject.repository.UserRepository;
import webit.Poject.security.UserPrincipal;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/owner-requests")
@RequiredArgsConstructor
public class OwnerApplicationController {

    private final OwnerRequestRepository ownerRequestRepository;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public ResponseEntity<?> submit(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OwnerApplicationRequest req
    ) {
        var user = userRepository.findById(principal.getUser().getUserId()).orElseThrow();
        if (user.getRole() != Role.CUSTOMER) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Only customers can request a business account"));
        }
        if (ownerRequestRepository.findByUserAndRequestStatus(user, OwnerRequestStatus.PENDING).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "You already have a pending owner request"));
        }

        OwnerRequest row = OwnerRequest.builder()
                .user(user)
                .businessName(req.businessName())
                .businessDescription(req.businessDescription())
                .location(req.location())
                .documentsUrl(req.documentsUrl())
                .requestStatus(OwnerRequestStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        ownerRequestRepository.save(row);

        user.setRequestedRole(Role.OWNER);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Owner request submitted", "id", row.getId()));
    }
}
