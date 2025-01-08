package itma.smesharikiback.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "smesharik")
public class Smesharik {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64, unique = true)
    private String login;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false, length = 128, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime lastActive = LocalDateTime.now();

    @Column(nullable = false, length = 64)
    private String salt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SmesharikRole role = SmesharikRole.USER;

    @Column(nullable = false)
    private Boolean isOnline = false;

}
