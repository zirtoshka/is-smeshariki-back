package itma.smesharikiback.domain.repository;

import itma.smesharikiback.domain.model.Notification;
import itma.smesharikiback.domain.model.Smesharik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySmesharik(Smesharik smesharik);
}













