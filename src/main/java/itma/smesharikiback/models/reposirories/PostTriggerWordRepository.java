package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.PostTriggerWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTriggerWordRepository extends JpaRepository<PostTriggerWord, Long> {
    List<PostTriggerWord> findByPostId(Long postId);
    List<PostTriggerWord> findByTriggerWordId(Long triggerWordId);
}

