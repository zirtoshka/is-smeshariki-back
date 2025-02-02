package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Data
@Entity
@Table(name = "application_for_treatment")
public class ApplicationForTreatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.NEW;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Smesharik doctor;

    @OneToMany(mappedBy = "applicationForTreatment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationForTreatmentPropensity> propensities;
}
