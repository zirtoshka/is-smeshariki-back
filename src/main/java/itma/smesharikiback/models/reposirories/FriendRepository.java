package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Friend;
import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByFollower(Smesharik follower);

    List<Friend> findByFollowee(Smesharik followee);

    boolean existsByFollowerAndFollowee(Smesharik follower, Smesharik followee);
}
