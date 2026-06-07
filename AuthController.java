package webit.Poject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import webit.Poject.dto.AuthResponse;
import webit.Poject.dto.LoginRequest;
import webit.Poject.dto.RegisterRequest;
import webit.Poject.dto.UserResponse;
import webit.Poject.model.User;
import webit.Poject.model.enums.Role;
import webit.Poject.model.enums.Status;
import webit.Poject.repository.UserRepository;
import webit.Poject.security.JwtService;
import webit.Poject.security.UserPrincipal;


import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email already registered"));
        }
        if (userRepository.existsByUsername(req.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Username already taken"));
        }

        User user = User.builder()
                .fullName(req.fullName())
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .phoneNumber(req.phoneNumber() != null && req.phoneNumber().isBlank() ? null : req.phoneNumber())
                .role(Role.CUSTOMER)
                .requestedRole(null)
                .status(Status.Active)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElse(null);
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password"));
        }
        if (user.getStatus() != Status.Active) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Account is not active"));
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, UserResponse.from(user)));
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return UserResponse.from(principal.getUser());
    }
}
