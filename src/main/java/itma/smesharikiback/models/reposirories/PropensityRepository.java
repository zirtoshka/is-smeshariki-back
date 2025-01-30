package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Propensity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropensityRepository extends JpaRepository<Propensity, Long>, JpaSpecificationExecutor<Propensity> {
}