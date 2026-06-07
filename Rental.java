package webit.Poject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "rentals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String userName;
    private String userPhone;
    private String jetskiId;
    private String jetskiModel;
    private String ownerId;
    private String ownerName;
    private String locationId;
    private String locationName;
    private int durationMinutes;
    private double amountSubtotal;
    private double vatAmount;
    private double amountTotal;
    private String currency;
    private String paymentMethod;
    private String paymentStatus;
    private Instant startTime;
    private Instant endTime;
    private Instant createdAt;
}
