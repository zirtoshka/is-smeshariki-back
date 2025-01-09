package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table(name = "application_for_treatment_propensity")
public class ApplicationForTreatmentPropensity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "application_for_treatment_id", nullable = false)
    private ApplicationForTreatment applicationForTreatment;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "propensity_id", nullable = false)
    private Propensity propensity;
}
