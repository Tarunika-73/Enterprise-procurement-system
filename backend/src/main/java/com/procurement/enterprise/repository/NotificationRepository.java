package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.Notification;
import com.procurement.enterprise.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndIsDeletedFalse(Long id);
    Page<Notification> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<Notification> findByUserIdAndIsReadAndIsDeletedFalse(Long userId, Boolean isRead, Pageable pageable);
    Page<Notification> findByUserIdAndTypeAndIsDeletedFalse(Long userId, NotificationType type, Pageable pageable);
    long countByUserIdAndIsReadFalseAndIsDeletedFalse(Long userId);
}
