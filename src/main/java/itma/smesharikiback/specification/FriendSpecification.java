package itma.smesharikiback.specification;

import itma.smesharikiback.models.Friend;
import itma.smesharikiback.models.FriendStatus;
import itma.smesharikiback.models.Smesharik;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class FriendSpecification {
    public static Specification<Friend> hasNameOrLogin(String nameOrLogin) {
        return (root, query, criteriaBuilder) -> {
            if (nameOrLogin == null || nameOrLogin.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("followee").get("name"), "%" + nameOrLogin + "%"),
                    criteriaBuilder.like(root.get("followee").get("login"), "%" + nameOrLogin + "%"),
                    criteriaBuilder.like(root.get("follower").get("name"), "%" + nameOrLogin + "%"),
                    criteriaBuilder.like(root.get("follower").get("login"), "%" + nameOrLogin + "%")

            );
        };
    }

    public static Specification<Friend> hasStatusaAndId(FriendStatus status, Smesharik followee, Smesharik follower) {
        return (root, query, criteriaBuilder) -> {
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), status);
            System.out.println(followee);
            System.out.println(follower);
            Predicate followeePredicate = followee != null ? criteriaBuilder.equal(root.get("followee"), followee) : criteriaBuilder.conjunction();
            Predicate followerPredicate = follower != null ? criteriaBuilder.equal(root.get("follower"), follower) : criteriaBuilder.conjunction();
            if (followee != null && follower != null) {
                return criteriaBuilder.and(statusPredicate, criteriaBuilder.or(followeePredicate, followerPredicate));
            }
            return criteriaBuilder.and(statusPredicate,followeePredicate, followerPredicate);
        };
    }
}
