package webit.Poject.dto;

import webit.Poject.model.User;
import webit.Poject.model.enums.Role;


public record UserResponse(
        Long userId,
        String fullName,
        String username,
        String email,
        String phoneNumber,
        Role role,
        Role requestedRole,
        String status
) {

    public static UserResponse from(User u) {
        return new UserResponse(
                u.getUserId(),
                u.getFullName(),
                u.getUsername(),
                u.getEmail(),
                u.getPhoneNumber(),
                u.getRole(),
                u.getRequestedRole(),
                u.getStatus() != null ? u.getStatus().name() : null
        );
    }
}
