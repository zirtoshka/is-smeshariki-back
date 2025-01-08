package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "trigger_word")
public class TriggerWord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 64)
    private String word;

    @ManyToOne
    @JoinColumn(name = "propensity_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Propensity propensity;
}
