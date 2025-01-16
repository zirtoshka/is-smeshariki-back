package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Carrot;
import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
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