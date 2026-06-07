package webit.Poject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webit.Poject.model.OwnerRequest;
import webit.Poject.model.User;
import webit.Poject.model.enums.OwnerRequestStatus;


import java.util.List;
import java.util.Optional;

public interface OwnerRequestRepository extends JpaRepository<OwnerRequest, Long> {

    Optional<OwnerRequest> findByUserAndRequestStatus(User user, OwnerRequestStatus status);

    List<OwnerRequest> findAllByRequestStatusOrderByCreatedAtDesc(OwnerRequestStatus status);

    List<OwnerRequest> findAllByOrderByCreatedAtDesc();

    @Query("select r from OwnerRequest r join fetch r.user order by r.createdAt desc")
    List<OwnerRequest> findAllJoinUserOrderByCreatedAtDesc();
}
