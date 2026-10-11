package com.luma.repository;

import com.luma.model.OpenWhenCapsule;
import com.luma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpenWhenCapsuleRepository extends JpaRepository<OpenWhenCapsule, Long> {
    // Capsules created by the user (sent or self-targeted)
    List<OpenWhenCapsule> findByCreatorOrderByCreatedAtDesc(User creator);

    // Capsules addressed to the user by others
    @Query("SELECT c FROM OpenWhenCapsule c WHERE LOWER(c.recipientUsername) = LOWER(:username) AND c.creator != :user ORDER BY c.createdAt DESC")
    List<OpenWhenCapsule> findReceivedCapsules(@Param("username") String username, @Param("user") User user);

    long countByCreator(User creator);
}
