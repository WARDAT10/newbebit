package webit.Poject.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "penalties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rentalId;
    private String userId;
    private String userName;
    private String jetskiModel;
    private String ownerId;
    private int minutesExceeded;
    private double amount;
    private String status;
    private Instant createdAt;
}
