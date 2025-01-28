package itma.smesharikiback.models.reposirories;


import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SmesharikRepository extends JpaRepository<Smesharik, Long> {

    @Query(
            "SELECT s FROM Smesharik s " +
                    "LEFT JOIN SmesharikBan b ON s.id = b.id " +
                    "WHERE (b.endDate <= current_timestamp OR b.id IS NULL) " +
                    "AND s.login = :login"
    )
    Optional<Smesharik> findByLogin(String login);

    @Query(
            "SELECT s FROM Smesharik s " +
                    "LEFT JOIN SmesharikBan b ON s.id = b.id " +
                    "WHERE (b.endDate <= current_timestamp OR b.id IS NULL) " +
                    "AND s.email = :email"
    )
    Optional<Smesharik> findByEmail(String email);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}
