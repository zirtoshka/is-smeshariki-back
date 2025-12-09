package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.Carrot;
import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarrotRepository extends JpaRepository<Carrot, Long> {
    List<Carrot> findBySmesharik(Smesharik smesharik);
    List<Carrot> findByPostId(Long postId);
    List<Carrot> findByCommentId(Long commentId);

    Optional<Carrot> findBySmesharikAndPost(Smesharik smesharik, Post post);
    Optional<Carrot> findBySmesharikAndComment(Smesharik smesharik, Comment comment);
}












