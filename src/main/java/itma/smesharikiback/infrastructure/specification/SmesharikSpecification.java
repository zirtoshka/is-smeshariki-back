package itma.smesharikiback.infrastructure.specification;

import itma.smesharikiback.domain.model.Smesharik;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SmesharikSpecification {

    public static Specification<Smesharik> hasNameOrLogin(String nameOrLogin) {
        return (root, query, criteriaBuilder) -> {
            if (nameOrLogin == null || nameOrLogin.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("name"), "%" + nameOrLogin + "%"),
                    criteriaBuilder.like(root.get("login"), "%" + nameOrLogin + "%")
            );
        };
    }

    public static Specification<Smesharik> hasRoles(List<String> roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("role").in(roles);
        };
    }
}














