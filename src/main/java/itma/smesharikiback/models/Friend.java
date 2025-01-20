package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "friend")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "followee_id", nullable = false)
    private Smesharik followee;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "follower_id", nullable = false)
    private Smesharik follower;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}
