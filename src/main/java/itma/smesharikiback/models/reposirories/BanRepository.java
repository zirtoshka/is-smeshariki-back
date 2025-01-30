package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Ban;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BanRepository extends JpaRepository<Ban, Long>, JpaSpecificationExecutor<Ban> {
    List<Ban> findBySmesharikId(Long smesharikId);
    List<Ban> findByPostId(Long postId);
    List<Ban> findByCommentId(Long commentId);

    @NotNull Page<Ban> findAll(Specification<Ban> specification, @NotNull Pageable pageable);
}