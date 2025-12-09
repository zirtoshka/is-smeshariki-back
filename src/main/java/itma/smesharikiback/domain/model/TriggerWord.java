package itma.smesharikiback.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "trigger_word")
public class TriggerWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 64)
    private String word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propensity_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Propensity propensity;
}













