package com.procurement.enterprise.repository;

import com.procurement.enterprise.entity.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    Optional<LoginHistory> findByIdAndIsDeletedFalse(Long id);
    Page<LoginHistory> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<LoginHistory> findAllByIsDeletedFalse(Pageable pageable);
    Page<LoginHistory> findByStatusAndIsDeletedFalse(String status, Pageable pageable);
}
