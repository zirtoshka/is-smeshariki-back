package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.TriggerWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TriggerWordRepository extends JpaRepository<TriggerWord, Long> {
    List<TriggerWord> findByPropensityId(Long propensityId);
}
