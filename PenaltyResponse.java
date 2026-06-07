package webit.Poject.dto;

import webit.Poject.model.Penalty;

import java.time.Instant;

public record PenaltyResponse(
        String id,
        String rentalId,
        String userId,
        String userName,
        String jetskiModel,
        String ownerId,
        int minutesExceeded,
        double amount,
        String status,
        Instant createdAt
) {

    public static PenaltyResponse from(Penalty p) {
        return new PenaltyResponse(
                String.valueOf(p.getId()),
                p.getRentalId(),
                p.getUserId(),
                p.getUserName(),
                p.getJetskiModel(),
                p.getOwnerId(),
                p.getMinutesExceeded(),
                p.getAmount(),
                p.getStatus(),
                p.getCreatedAt()
        );
    }
}
