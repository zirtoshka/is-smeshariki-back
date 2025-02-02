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

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, " +
            "COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN p.author " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "WHERE p.id = :id " +
            "GROUP BY p.id, p.author")
    Optional<PostWithCarrotsDto> findByIdWithCarrots(Long id);

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, " +
            "COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN p.author " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "GROUP BY p.id, p.author")
    Page<PostWithCarrotsDto> findPosts(Specification<Post> specification, Pageable pageable);

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, " +
            "COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN p.author " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "WHERE p.author = :author " +
            "GROUP BY p.id, p.author")
    Page<PostWithCarrotsDto> findPostsByAuthorWithCarrots(Smesharik author, Specification<Post> specification, Pageable pageable);

    @Query("SELECT new itma.smesharikiback.models.dto.PostWithCarrotsDto(p.id, p.author, p.isDraft, " +
            "p.text, p.isPrivate, p.publicationDate, p.pathToImage, p.creationDate, COUNT(cl)) " +
            "FROM Post p " +
            "LEFT JOIN p.author " +
            "LEFT JOIN Carrot cl ON cl.post = p " +
            "LEFT JOIN PostBan b ON p.id = b.id " +
            "WHERE p.isPrivate = false " +
            "AND p.isDraft = false " +
            "AND p.author != :currentSmesharik " +
            "AND (b.endDate <= current_timestamp OR b.id IS NULL) " +
            "AND EXISTS (SELECT 1 FROM Friend f WHERE (f.follower = :currentSmesharik OR f.followee = :currentSmesharik) " +
            "AND f.status = 'FRIENDS' AND (f.follower = p.author OR f.followee = p.author)) " +
            "GROUP BY p.id, p.author")
    Page<PostWithCarrotsDto> findPublicPostsForSmesharik(@Param("currentSmesharik") Smesharik currentSmesharik, Specification<Post> specification, Pageable pageable);

}
