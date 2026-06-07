package webit.Poject.dto;

import jakarta.validation.constraints.Size;

public record UpdatePhoneRequest(@Size(max = 20) String phoneNumber) {
}
