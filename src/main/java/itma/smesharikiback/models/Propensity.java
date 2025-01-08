package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "propensity")
public class Propensity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 1024)
    private String description;
}
