package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.Propensity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PropensityRepository extends JpaRepository<Propensity, Long>, JpaSpecificationExecutor<Propensity> {
}












