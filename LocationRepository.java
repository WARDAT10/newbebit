package webit.Poject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webit.Poject.model.Location;


public interface LocationRepository extends JpaRepository<Location, String> {
}
