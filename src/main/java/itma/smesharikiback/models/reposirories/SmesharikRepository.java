package itma.smesharikiback.models.reposirories;


import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmesharikRepository extends JpaRepository<Smesharik, Long> {
    Optional<Smesharik> findByLogin(String login);
//    Optional<Smesharik> findByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}
