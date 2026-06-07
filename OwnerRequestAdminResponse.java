package webit.Poject.dto;

import webit.Poject.model.OwnerRequest;
import webit.Poject.model.enums.OwnerRequestStatus;


import java.time.Instant;

public record OwnerRequestAdminResponse(
        Long id,
        Long userId,
        String email,
        String businessName,
        String businessDescription,
        String location,
        String documentsUrl,
        OwnerRequestStatus requestStatus,
        Instant createdAt
) {

    public static OwnerRequestAdminResponse from(OwnerRequest r) {
        return new OwnerRequestAdminResponse(
                r.getId(),
                r.getUser().getUserId(),
                r.getUser().getEmail(),
                r.getBusinessName(),
                r.getBusinessDescription(),
                r.getLocation(),
                r.getDocumentsUrl(),
                r.getRequestStatus(),
                r.getCreatedAt()
        );
    }
}
