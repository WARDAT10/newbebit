package webit.Poject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webit.Poject.model.Jetski;


import java.util.List;

public interface JetskiRepository extends JpaRepository<Jetski, Long> {

    List<Jetski> findByLocationIdOrderByIdAsc(String locationId);

    List<Jetski> findByOwnerIdOrderByIdAsc(String ownerId);
}
