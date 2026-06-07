package webit.Poject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webit.Poject.model.Rental;


import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Rental> findByOwnerIdOrderByCreatedAtDesc(String ownerId);
}
