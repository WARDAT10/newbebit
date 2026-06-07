package webit.Poject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import webit.Poject.dto.UpdatePhoneRequest;
import webit.Poject.dto.UserResponse;
import webit.Poject.repository.UserRepository;
import webit.Poject.security.UserPrincipal;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;

    @PatchMapping("/me")
    @Transactional
    public UserResponse updatePhone(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdatePhoneRequest req
    ) {
        var user = userRepository.findById(principal.getUser().getUserId()).orElseThrow();
        user.setPhoneNumber(req.phoneNumber() != null && req.phoneNumber().isBlank() ? null : req.phoneNumber());
        userRepository.save(user);
        return UserResponse.from(user);
    }
}
