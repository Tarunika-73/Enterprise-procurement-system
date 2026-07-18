package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByIdAndIsDeletedFalse(Long id);
    Optional<UserSession> findBySessionTokenAndIsDeletedFalse(String sessionToken);
    Page<UserSession> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<UserSession> findAllByIsDeletedFalse(Pageable pageable);
    Page<UserSession> findByUserIdAndIsActiveAndIsDeletedFalse(Long userId, Boolean isActive, Pageable pageable);
}
