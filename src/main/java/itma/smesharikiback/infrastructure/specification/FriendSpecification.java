package itma.smesharikiback.infrastructure.specification;

import itma.smesharikiback.domain.model.Friend;
import itma.smesharikiback.domain.model.FriendStatus;
import itma.smesharikiback.domain.model.Smesharik;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

public class FriendSpecification {
    private static final Logger log = LoggerFactory.getLogger(FriendSpecification.class);

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
            log.debug("Filtering friends by status={} followee={} follower={}", status, followee, follower);
            Predicate followeePredicate = followee != null ? criteriaBuilder.equal(root.get("followee"), followee) : criteriaBuilder.conjunction();
            Predicate followerPredicate = follower != null ? criteriaBuilder.equal(root.get("follower"), follower) : criteriaBuilder.conjunction();
            if (followee != null && follower != null) {
                return criteriaBuilder.and(statusPredicate, criteriaBuilder.or(followeePredicate, followerPredicate));
            }
            return criteriaBuilder.and(statusPredicate,followeePredicate, followerPredicate);
        };
    }
}













