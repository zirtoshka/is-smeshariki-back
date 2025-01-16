package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.dto.CommentWithChildrenDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Post> {
    List<Comment> findByPost(Post post);
    List<Comment> findByParentComment(Comment parentComment);

    @Query("SELECT new itma.smesharikiback.models.dto.CommentWithChildrenDto(c.id, c.smesharik.id, c.post.id, " +
            "c.creationDate, c.parentComment.id, c.text, " +
            "CASE WHEN EXISTS (SELECT 1 FROM Comment c2 WHERE c2.parentComment = c) THEN true ELSE false END, " +
            "COUNT(cl)) " +
            "FROM Comment c " +
            "LEFT JOIN Carrot cl ON cl.comment = c " +
            "WHERE c.id = :id " +
            "GROUP BY c.id")
    Optional<CommentWithChildrenDto> findByIdWithChildren(Long id);

    @Query("SELECT new itma.smesharikiback.models.dto.CommentWithChildrenDto(c.id, c.smesharik.id, c.post.id, " +
            "c.creationDate, c.parentComment.id, c.text, " +
            "CASE WHEN EXISTS (SELECT 1 FROM Comment c2 WHERE c2.parentComment = c) THEN true ELSE false END, " +
            "COUNT(cl) ) " +
            "FROM Comment c " +
            "LEFT JOIN Carrot cl ON cl.comment = c " +
            "WHERE c.post = :post OR c.parentComment = :comment " +
            "GROUP BY c.id " +
            "ORDER BY c.creationDate DESC")
    Page<CommentWithChildrenDto> findCommentsByPostOrParentComment(Post post, Comment comment, Pageable pageRequest);

}