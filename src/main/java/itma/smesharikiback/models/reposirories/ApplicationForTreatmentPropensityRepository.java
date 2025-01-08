package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.ApplicationForTreatmentPropensity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationForTreatmentPropensityRepository extends JpaRepository<ApplicationForTreatmentPropensity, Long> {
    List<ApplicationForTreatmentPropensity> findByApplicationForTreatmentId(Long applicationId);
    List<ApplicationForTreatmentPropensity> findByPropensityId(Long propensityId);
}
