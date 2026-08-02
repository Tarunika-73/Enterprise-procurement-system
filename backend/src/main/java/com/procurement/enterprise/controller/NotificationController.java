package com.procurement.enterprise.controller;

import com.procurement.enterprise.dto.response.NotificationResponse;
import com.procurement.enterprise.entity.Notification;
import com.procurement.enterprise.entity.User;
import com.procurement.enterprise.exception.UnauthorizedException;
import com.procurement.enterprise.repository.NotificationRepository;
import com.procurement.enterprise.repository.UserRepository;
import com.procurement.enterprise.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/v1/notifications", "/notifications"})
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping("/my")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        User user = getCurrentUser();
        Page<NotificationResponse> page = notificationRepository
                .findByUserIdAndIsDeletedFalse(user.getId(), pageable)
                .map(this::mapToResponse);

        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully.", page));
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
