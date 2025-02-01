package itma.smesharikiback.specification;

import itma.smesharikiback.models.Complaint;
import itma.smesharikiback.models.GeneralStatus;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.services.CommonService;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ComplaintSpecification {

    public static Specification<Complaint> getComplaints(
            String description,
            List<GeneralStatus> statuses,
            Boolean isMine,
            Smesharik currentSmesharik
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"));
            }

            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            }

            if (isMine != null) {
                if (isMine) {
                    predicates.add(criteriaBuilder.equal(root.get("admin"), currentSmesharik));
                } else {
                    predicates.add(criteriaBuilder.or(criteriaBuilder.notEqual(root.get("admin"), currentSmesharik),
                            criteriaBuilder.isNull(root.get("admin"))));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
