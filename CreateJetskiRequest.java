package webit.Poject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateJetskiRequest(
        @NotBlank String model,
        @NotBlank String locationId,
        @NotNull @Min(0) Double priceUsd15min,
        @NotNull @Min(0) Integer priceTzs15min,
        String imageUrl
) {
}
