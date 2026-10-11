package com.luma.repository;

import com.luma.model.User;
import com.luma.model.VaultAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultAuditLogRepository extends JpaRepository<VaultAuditLog, Long> {
    List<VaultAuditLog> findByUserOrderByTimestampDesc(User user);
    List<VaultAuditLog> findTop20ByUserOrderByTimestampDesc(User user);
}
