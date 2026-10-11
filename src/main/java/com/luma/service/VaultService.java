package com.luma.service;

import com.luma.dto.AuthDto;
import com.luma.dto.VaultDto;
import com.luma.model.*;
import com.luma.repository.OpenWhenCapsuleRepository;
import com.luma.repository.UserRepository;
import com.luma.repository.VaultAuditLogRepository;
import com.luma.repository.VaultEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VaultService {

    @Autowired
    private VaultEntryRepository vaultEntryRepository;

    @Autowired
    private VaultAuditLogRepository vaultAuditLogRepository;

    @Autowired
    private OpenWhenCapsuleRepository openWhenCapsuleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public VaultDto.VaultEntryResponse createEntry(User owner, VaultDto.CreateVaultEntryRequest request, String ipAddress) {
        VaultEntry entry = new VaultEntry();
        entry.setOwner(owner);
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setType(request.getType() != null ? request.getType() : VaultEntryType.NOTE);
        entry.setMediaUrl(request.getMediaUrl());
        entry.setMoodEmoji(request.getMoodEmoji());
        entry.setVisibility(request.getVisibility() != null ? request.getVisibility() : VaultVisibility.PRIVATE);
        entry.setTrustedUsernames(request.getTrustedUsernames());
        entry.setExpiresAt(request.getExpiresAt());
        entry.setEncrypted(request.isEncrypted());

        VaultEntry saved = vaultEntryRepository.save(entry);

        // Record audit log
        recordAuditLog(owner, "ENTRY_CREATED", "Created vault item: \"" + saved.getTitle() + "\" (" + saved.getVisibility() + ")", ipAddress);

        // If shared with trusted circles, notify users
        if (saved.getVisibility() == VaultVisibility.TRUSTED_CIRCLE && saved.getTrustedUsernames() != null) {
            String[] trusted = saved.getTrustedUsernames().split(",");
            for (String uname : trusted) {
                String clean = uname.trim().toLowerCase().replace("@", "");
                userRepository.findByUsername(clean).ifPresent(target -> {
                    notificationService.createNotification(
                            target,
                            owner,
                            "VAULT_SHARED",
                            "@" + owner.getUsername() + " granted you access to a private Vault memory.",
                            saved.getId()
                    );
                });
            }
        }

        return new VaultDto.VaultEntryResponse(saved);
    }

    public List<VaultDto.VaultEntryResponse> getMyEntries(User user, VaultEntryType type) {
        List<VaultEntry> list;
        if (type != null) {
            list = vaultEntryRepository.findByOwnerAndTypeOrderByCreatedAtDesc(user, type);
        } else {
            list = vaultEntryRepository.findByOwnerOrderByCreatedAtDesc(user);
        }

        return list.stream()
                .map(VaultDto.VaultEntryResponse::new)
                .collect(Collectors.toList());
    }

    public List<VaultDto.VaultEntryResponse> getSharedEntries(User user) {
        List<VaultEntry> shared = vaultEntryRepository.findSharedWithUser(user, user.getUsername(), LocalDateTime.now());
        return shared.stream()
                .map(VaultDto.VaultEntryResponse::new)
                .collect(Collectors.toList());
    }

    public VaultDto.VaultEntryResponse getEntryById(Long id, User user, String ipAddress) {
        VaultEntry entry = vaultEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vault entry not found"));

        // Check permission
        boolean isOwner = entry.getOwner().getId().equals(user.getId());
        boolean isTrusted = false;

        if (!isOwner) {
            if (entry.getTrustedUsernames() != null) {
                List<String> allowed = Arrays.stream(entry.getTrustedUsernames().split(","))
                        .map(s -> s.trim().toLowerCase().replace("@", ""))
                        .toList();
                if (allowed.contains(user.getUsername().toLowerCase())) {
                    if (entry.getVisibility() == VaultVisibility.TRUSTED_CIRCLE) {
                        isTrusted = true;
                    } else if (entry.getVisibility() == VaultVisibility.TEMPORARY_ACCESS) {
                        isTrusted = entry.getExpiresAt() == null || entry.getExpiresAt().isAfter(LocalDateTime.now());
                    }
                }
            }
        }

        if (!isOwner && !isTrusted) {
            throw new SecurityException("Access denied: You do not have permission to view this Vault memory.");
        }

        recordAuditLog(user, "ENTRY_VIEWED", "Viewed vault item: \"" + entry.getTitle() + "\"", ipAddress);

        return new VaultDto.VaultEntryResponse(entry);
    }

    @Transactional
    public void deleteEntry(Long id, User user, String ipAddress) {
        VaultEntry entry = vaultEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vault entry not found"));

        if (!entry.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("Only the owner can delete this Vault entry.");
        }

        recordAuditLog(user, "ENTRY_DELETED", "Deleted vault item: \"" + entry.getTitle() + "\"", ipAddress);
        vaultEntryRepository.delete(entry);
    }

    public VaultDto.VaultStatsResponse getVaultStats(User user) {
        List<VaultEntry> myEntries = vaultEntryRepository.findByOwnerOrderByCreatedAtDesc(user);
        long total = myEntries.size();
        long encrypted = myEntries.stream().filter(VaultEntry::isEncrypted).count();
        long shared = myEntries.stream().filter(e -> e.getVisibility() != VaultVisibility.PRIVATE).count();
        long capsules = openWhenCapsuleRepository.countByCreator(user);

        return new VaultDto.VaultStatsResponse(total, encrypted, shared, capsules);
    }

    public void recordAuditLog(User user, String action, String details, String ipAddress) {
        VaultAuditLog log = new VaultAuditLog(user, action, details, ipAddress != null ? ipAddress : "127.0.0.1");
        vaultAuditLogRepository.save(log);
    }

    public List<VaultAuditLog> getAuditLogs(User user) {
        return vaultAuditLogRepository.findTop20ByUserOrderByTimestampDesc(user);
    }
}
