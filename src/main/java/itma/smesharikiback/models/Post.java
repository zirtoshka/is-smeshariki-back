package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Smesharik author;

    @Column(nullable = false)
    private Boolean isDraft = true;

    @Column(length = 4096)
    private String text;

    @Column(nullable = false, name = "private")
    private Boolean isPrivate = true;

    private LocalDateTime publicationDate;

    @Column(length = 256)
    private String pathToImage;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();


}
