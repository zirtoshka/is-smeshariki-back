package itma.smesharikiback.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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













