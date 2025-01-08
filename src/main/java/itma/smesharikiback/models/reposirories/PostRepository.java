package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(Smesharik author);

    List<Post> findByIsDraftFalseOrderByPublicationDateDesc();

    List<Post> findByIsPrivateFalseAndIsDraftFalseOrderByPublicationDateDesc();
}