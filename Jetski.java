package webit.Poject.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jetskis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jetski {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    @Column(nullable = false)
    private String ownerId;

    private String ownerName;

    @Column(nullable = false)
    private String locationId;

    /** available | rented | maintenance */
    @Column(nullable = false)
    @Builder.Default
    private String status = "available";

    private double priceUsd15min;
    private int priceTzs15min;
    private String imageUrl;
    private Double boundaryLat;
    private Double boundaryLng;
    private Double boundaryRadiusKm;
}
