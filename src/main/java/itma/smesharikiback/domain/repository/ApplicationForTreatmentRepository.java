package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.ApplicationForTreatment;
import itma.smesharikiback.domain.model.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ApplicationForTreatmentRepository extends JpaRepository<ApplicationForTreatment, Long>,
        JpaSpecificationExecutor<ApplicationForTreatment> {
    List<ApplicationForTreatment> findByDoctor(Smesharik doctor);
    List<ApplicationForTreatment> findByStatus(String status);
}













