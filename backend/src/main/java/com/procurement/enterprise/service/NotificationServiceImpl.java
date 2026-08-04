package com.procurement.enterprise.service;

import com.procurement.enterprise.dto.response.NotificationResponse;
import com.procurement.enterprise.entity.Notification;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.enums.NotificationType;
import com.procurement.enterprise.exception.ResourceNotFoundException;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.NotificationRepository;
import com.procurement.enterprise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Boolean isRead, NotificationType type, Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Notification> notifications;

        if (isRead != null) {
            notifications = notificationRepository.findByUserIdAndIsReadAndIsDeletedFalse(currentUser.getId(), isRead, pageable);
        } else if (type != null) {
            notifications = notificationRepository.findByUserIdAndTypeAndIsDeletedFalse(currentUser.getId(), type, pageable);
        } else {
            notifications = notificationRepository.findByUserIdAndIsDeletedFalse(currentUser.getId(), pageable);
        }

        return notifications.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUnreadCount() {
        User currentUser = getCurrentUser();
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalseAndIsDeletedFalse(currentUser.getId());
        return Map.of("unreadCount", unreadCount);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to modify this notification.");
        }

        notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        log.info("Notification {} marked as read for user {}", notificationId, currentUser.getId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        Page<Notification> unreadPage = notificationRepository.findByUserIdAndIsReadAndIsDeletedFalse(
                currentUser.getId(), false, Pageable.unpaged());

        List<Notification> unreadList = unreadPage.getContent();
        if (!unreadList.isEmpty()) {
            unreadList.forEach(n -> n.setIsRead(true));
            notificationRepository.saveAll(unreadList);
            log.info("Marked {} notifications as read for user {}", unreadList.size(), currentUser.getId());
        }
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        User currentUser = getCurrentUser();
        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this notification.");
        }

        notification.setIsDeleted(true);
        notificationRepository.save(notification);
        log.info("Notification {} soft deleted for user {}", notificationId, currentUser.getId());
    }

    @Override
    @Transactional
    public Notification createNotification(User user, NotificationType type, String subject, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type != null ? type : NotificationType.SYSTEM)
                .subject(subject)
                .message(message)
                .isRead(false)
                .isDeleted(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification {} created for user {}", saved.getId(), user.getId());
        return saved;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required.");
        }
        return userRepository.findByEmailAndIsDeletedFalse(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found."));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
