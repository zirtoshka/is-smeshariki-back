package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Ban;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanRepository extends JpaRepository<Ban, Long> {
    List<Ban> findBySmesharikId(Long smesharikId);
    List<Ban> findByPostId(Long postId);
    List<Ban> findByCommentId(Long commentId);
}