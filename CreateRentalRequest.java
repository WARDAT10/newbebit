package webit.Poject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRentalRequest(
        @NotBlank String jetskiId,
        @NotBlank String locationId,
        @NotBlank String currency,
        @NotBlank String paymentMethod,
        @NotNull @Min(1) Integer durationMinutes
) {
}
