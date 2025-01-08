package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "application_for_treatment_propensity")
public class ApplicationForTreatmentPropensity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_for_treatment_id", nullable = false)
    private ApplicationForTreatment applicationForTreatment;

    @ManyToOne
    @JoinColumn(name = "propensity_id", nullable = false)
    private Propensity propensity;
}
