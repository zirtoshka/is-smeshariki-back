package itma.smesharikiback.models.reposirories;

import itma.smesharikiback.models.Notification;
import itma.smesharikiback.models.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySmesharik(Smesharik smesharik);
}
