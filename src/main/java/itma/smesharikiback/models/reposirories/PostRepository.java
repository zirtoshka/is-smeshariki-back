package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.dto.PostWithCarrotsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    List<Post> findByAuthor(Smesharik author);

    List<Post> findByIsDraftFalseOrderByPublicationDateDesc();

    List<Post> findByIsPrivateFalseAndIsDraftFalseOrderByPublicationDateDesc();

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author.id, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, " +
            "COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "WHERE p.id = :id " +
            "GROUP BY p.id")
    Optional<PostWithCarrotsDto> findByIdWithCarrots(Long id);

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author.id, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, " +
            "COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "GROUP BY p.id")
    Page<PostWithCarrotsDto> findPosts(Specification<Post> specification, Pageable pageable);

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author.id, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "WHERE ((p.author = :currentSmesharik) OR EXISTS (" +
            "  SELECT 1 FROM Friend f WHERE " +
            " ((f.follower = :currentSmesharik OR f.followee = :currentSmesharik) " +
            "  AND f.status = 'FRIENDS' AND (p.author = f.follower OR p.author = f.followee)" +
            "))) AND p.isPrivate = false " +
            "GROUP BY p.id")
    Page<PostWithCarrotsDto> findPublicPostsForSmesharik(@Param("currentSmesharik") Smesharik currentSmesharik, Specification<Post> specification, Pageable pageable);

}
