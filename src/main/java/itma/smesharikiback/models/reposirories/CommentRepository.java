package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findByParentComment(Comment parentComment);
}