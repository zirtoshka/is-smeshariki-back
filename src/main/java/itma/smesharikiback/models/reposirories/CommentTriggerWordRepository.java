package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.CommentTriggerWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentTriggerWordRepository extends JpaRepository<CommentTriggerWord, Long> {
    List<CommentTriggerWord> findByCommentId(Long commentId);
    List<CommentTriggerWord> findByTriggerWordId(Long triggerWordId);
}
