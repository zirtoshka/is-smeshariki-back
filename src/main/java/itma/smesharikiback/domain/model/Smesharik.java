package itma.smesharikiback.domain.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
@Table(name = "smesharik")
public class Smesharik implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SmesharikRole role = SmesharikRole.USER;

    @Column(nullable = false)
    private Boolean isOnline = false;

    @Column(nullable = false, length = 16)
    private String color;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return getLogin();
    }
}













