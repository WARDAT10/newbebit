package webit.Poject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OwnerApplicationRequest(
        @NotBlank @Size(max = 200) String businessName,
        @Size(max = 2000) String businessDescription,
        @Size(max = 500) String location,
        @Size(max = 1000) String documentsUrl
) {
}
