package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ban")
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 512)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "smesharik_id")
    private Smesharik smesharik;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private LocalDateTime endDate = LocalDateTime.now().plusHours(1);

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();
}
