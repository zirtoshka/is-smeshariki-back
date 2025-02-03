package itma.smesharikiback.models.reposirories;


import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SmesharikRepository extends JpaRepository<Smesharik, Long>, JpaSpecificationExecutor<Smesharik> {

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

    @Modifying
    @Query(value = "update smesharik as s " +
            "set is_online = false " +
            "where s.is_online = true and s.last_active + interval '5 minutes' < now()",
            nativeQuery = true)
    void updateIsOnline();
}
