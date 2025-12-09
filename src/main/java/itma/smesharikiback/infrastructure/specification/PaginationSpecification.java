package itma.smesharikiback.infrastructure.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class PaginationSpecification {
    public static <T> Specification<T> filterByMultipleFields(String filter) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (filter != null && !filter.isEmpty()) {
                for (var attribute : root.getModel().getAttributes()) {
                    if (attribute.getJavaType().equals(String.class)) {
                        predicates.add(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(root.get(attribute.getName())),
                                        "%" + filter.toLowerCase() + "%"
                                )
                        );
                    }
                }
                predicate = criteriaBuilder.or(
                        predicates.toArray(new Predicate[0])
                );
            }
            return predicate;
        };
    }
}













