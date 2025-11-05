package cymind.repository;

import cymind.model.ResourceNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ResourceNotificationRepository extends JpaRepository<ResourceNotification, Long> {
    List<ResourceNotification> findTop2ByOrderByTimestampDesc();
}

