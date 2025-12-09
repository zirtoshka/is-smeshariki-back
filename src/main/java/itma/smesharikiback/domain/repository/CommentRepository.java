package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.application.dto.CommentWithChildrenDto;
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

    @Query("SELECT new itma.smesharikiback.application.dto.CommentWithChildrenDto(" +
            "c.id, s, p, " + // Используем псевдонимы s, p, pc
            "c.creationDate, pc, c.text, " +
            "CASE WHEN EXISTS (SELECT 1 FROM Comment c2 WHERE c2.parentComment = c) THEN true ELSE false END, " +
            "COUNT(cl.id)) " +
            "FROM Comment c " +
            "LEFT JOIN c.smesharik s " +
            "LEFT JOIN c.post p " +
            "LEFT JOIN c.parentComment pc " +
            "LEFT JOIN Carrot cl ON cl.comment = c " +
            "WHERE c.id = :id " +
            "GROUP BY c.id, s, p, pc, c.creationDate, c.text")
    Optional<CommentWithChildrenDto> findByIdWithChildren(Long id);

    @Query("SELECT new itma.smesharikiback.application.dto.CommentWithChildrenDto(c.id, c.smesharik, c.post, " +
            "c.creationDate, c.parentComment, c.text, " +
            "CASE WHEN EXISTS (SELECT 1 FROM Comment c2 WHERE c2.parentComment = c) THEN true ELSE false END, " +
            "COUNT(cl) ) " +
            "FROM Comment c " +
            "LEFT JOIN c.smesharik s " +
            "LEFT JOIN c.post p " +
            "LEFT JOIN c.parentComment pc " +
            "LEFT JOIN Carrot cl ON cl.comment = c " +
            "LEFT JOIN CommentBan b ON c.id = b.id " +
            "WHERE (b.endDate <= current_timestamp OR b.id IS NULL) " +
            "AND (c.post = :post OR c.parentComment = :comment) " +
            "GROUP BY c.id, c.smesharik, c.post, c.parentComment " +
            "ORDER BY c.creationDate DESC")
    Page<CommentWithChildrenDto> findCommentsByPostOrParentComment(Post post, Comment comment, Pageable pageRequest);

}












