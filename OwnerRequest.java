package webit.Poject.model;

import jakarta.persistence.*;
import lombok.*;
import webit.Poject.model.enums.OwnerRequestStatus;


import java.time.Instant;

@Entity
@Table(name = "owner_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(length = 2000)
    private String businessDescription;

    @Column(length = 500)
    private String location;

    /** Optional URL or path to uploaded verification documents */
    @Column(length = 1000)
    private String documentsUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OwnerRequestStatus requestStatus = OwnerRequestStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
