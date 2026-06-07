package webit.Poject.dto;



import webit.Poject.model.Rental;

import java.time.Instant;

public record RentalResponse(
        String id,
        String userId,
        String userName,
        String userPhone,
        String jetskiId,
        String jetskiModel,
        String ownerId,
        String ownerName,
        String locationId,
        String locationName,
        int durationMinutes,
        double amountSubtotal,
        double vatAmount,
        double amountTotal,
        String currency,
        String paymentMethod,
        String paymentStatus,
        Instant startTime,
        Instant endTime,
        Instant createdAt
) {

    public static RentalResponse from(Rental r) {
        return new RentalResponse(
                String.valueOf(r.getId()),
                r.getUserId(),
                r.getUserName(),
                r.getUserPhone(),
                r.getJetskiId(),
                r.getJetskiModel(),
                r.getOwnerId(),
                r.getOwnerName(),
                r.getLocationId(),
                r.getLocationName(),
                r.getDurationMinutes(),
                r.getAmountSubtotal(),
                r.getVatAmount(),
                r.getAmountTotal(),
                r.getCurrency(),
                r.getPaymentMethod(),
                r.getPaymentStatus(),
                r.getStartTime(),
                r.getEndTime(),
                r.getCreatedAt()
        );
    }
}
