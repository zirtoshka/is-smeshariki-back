package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "application_for_treatment")
public class ApplicationForTreatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeneralStatus status = GeneralStatus.NEW;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Smesharik doctor;
}
