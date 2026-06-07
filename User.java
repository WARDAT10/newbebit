package webit.Poject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import webit.Poject.model.enums.Gender;
import webit.Poject.model.enums.Role;
import webit.Poject.model.enums.Status;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "name", nullable = false)
    private String fullName;

    @NotBlank
    @Size(min = 3, max = 20)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Size(min = 8)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    /** Effective role for authorization (never chosen at public signup). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.CUSTOMER;

    /** Set when a user applies for OWNER (pending admin approval). */
    @Enumerated(EnumType.STRING)
    private Role requestedRole;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.Active;

    private String nationality;
    private String profileImage;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public User(Long userId) {
        this.userId = userId;
    }
}
