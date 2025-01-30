package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.TriggerWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TriggerWordRepository extends JpaRepository<TriggerWord, Long>, JpaSpecificationExecutor<TriggerWord> {
    List<TriggerWord> findByPropensityId(Long propensityId);
}
