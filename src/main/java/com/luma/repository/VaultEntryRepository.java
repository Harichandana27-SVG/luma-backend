package com.luma.repository;

import com.luma.model.User;
import com.luma.model.VaultEntry;
import com.luma.model.VaultEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VaultEntryRepository extends JpaRepository<VaultEntry, Long> {
    List<VaultEntry> findByOwnerOrderByCreatedAtDesc(User owner);

    List<VaultEntry> findByOwnerAndTypeOrderByCreatedAtDesc(User owner, VaultEntryType type);

    long countByOwner(User owner);

    @Query("SELECT v FROM VaultEntry v WHERE v.owner != :user AND (" +
           "(v.visibility = com.luma.model.VaultVisibility.TRUSTED_CIRCLE AND LOWER(v.trustedUsernames) LIKE LOWER(CONCAT('%', :username, '%'))) OR " +
           "(v.visibility = com.luma.model.VaultVisibility.TEMPORARY_ACCESS AND LOWER(v.trustedUsernames) LIKE LOWER(CONCAT('%', :username, '%')) AND (v.expiresAt IS NULL OR v.expiresAt > :now))" +
           ") ORDER BY v.createdAt DESC")
    List<VaultEntry> findSharedWithUser(@Param("user") User user, @Param("username") String username, @Param("now") LocalDateTime now);
}
