package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.NotificationResponse;
import com.procurement.enterprise.entity.Notification;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service interface for Notification management.
 */
public interface NotificationService {

    Page<NotificationResponse> getMyNotifications(Boolean isRead, NotificationType type, Pageable pageable);

    Map<String, Object> getUnreadCount();

    NotificationResponse markAsRead(Long notificationId);

    void markAllAsRead();

    void deleteNotification(Long notificationId);

    Notification createNotification(User user, NotificationType type, String subject, String message);
}
