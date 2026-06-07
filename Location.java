package webit.Poject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String name;

    private int sortOrder;

    private String imageUrl;
}
