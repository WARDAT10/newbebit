package webit.Poject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webit.Poject.model.Penalty;


import java.util.List;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

    List<Penalty> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Penalty> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    List<Penalty> findByRentalId(String rentalId);
}
