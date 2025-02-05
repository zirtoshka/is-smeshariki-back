package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Friend;
import itma.smesharikiback.models.FriendStatus;
import itma.smesharikiback.models.Smesharik;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, JpaSpecificationExecutor<Friend> {
    List<Friend> findByFollower(Smesharik follower, Pageable pageable);

    List<Friend> findByFollowee(Smesharik followee);

    boolean existsByFollowerAndFollowee(Smesharik follower, Smesharik followee);

    Optional<Friend> findByFollowerAndFollowee(Smesharik follower, Smesharik followee);

    @Query("SELECT f FROM Friend f WHERE (f.followee = :followee OR f.follower = :follower) AND f.status = :status")
    Page<Friend> findByFolloweeOrFollowerAndStatus(
            @Param("followee") Smesharik followee,
            @Param("follower") Smesharik follower,
            @Param("status") FriendStatus status,
            Specification<Friend> specification,
            Pageable pageable
    );


}
