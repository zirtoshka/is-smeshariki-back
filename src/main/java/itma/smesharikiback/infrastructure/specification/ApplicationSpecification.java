package itma.smesharikiback.infrastructure.specification;

import itma.smesharikiback.domain.model.ApplicationForTreatment;
import itma.smesharikiback.domain.model.GeneralStatus;
import itma.smesharikiback.domain.model.Smesharik;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ApplicationSpecification {
    public static Specification<ApplicationForTreatment> getComplaints(
            List<GeneralStatus> statuses,
            Boolean isMine,
            Smesharik currentSmesharik
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            if (isMine != null) {
                if (isMine) {
                    predicates.add(criteriaBuilder.equal(root.get("doctor"), currentSmesharik));
                } else {
                    predicates.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("doctor"), currentSmesharik),
                            criteriaBuilder.isNull(root.get("doctor"))));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}













