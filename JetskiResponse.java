package webit.Poject.dto;

import webit.Poject.model.Jetski;

public record JetskiResponse(
        String id,
        String model,
        String ownerId,
        String ownerName,
        String locationId,
        String status,
        double priceUsd15min,
        int priceTzs15min,
        String imageUrl,
        Double boundaryLat,
        Double boundaryLng,
        Double boundaryRadiusKm
) {

    public static JetskiResponse from(Jetski j) {
        return new JetskiResponse(
                String.valueOf(j.getId()),
                j.getModel(),
                j.getOwnerId(),
                j.getOwnerName(),
                j.getLocationId(),
                j.getStatus(),
                j.getPriceUsd15min(),
                j.getPriceTzs15min(),
                j.getImageUrl(),
                j.getBoundaryLat(),
                j.getBoundaryLng(),
                j.getBoundaryRadiusKm()
        );
    }
}
