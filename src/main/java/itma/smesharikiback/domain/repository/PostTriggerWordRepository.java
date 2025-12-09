package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.PostTriggerWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTriggerWordRepository extends JpaRepository<PostTriggerWord, Long> {
    List<PostTriggerWord> findByPostId(Long postId);
    List<PostTriggerWord> findByTriggerWordId(Long triggerWordId);
}














