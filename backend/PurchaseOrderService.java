package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // TODO: add custom query methods as needed
}
