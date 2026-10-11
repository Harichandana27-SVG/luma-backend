package com.luma.dto;

import com.luma.model.VaultEntry;
import com.luma.model.VaultEntryType;
import com.luma.model.VaultVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class VaultDto {

    public static class CreateVaultEntryRequest {
        @NotBlank(message = "Title is required")
        @Size(max = 150)
        private String title;

        private String content;

        @NotNull(message = "Entry type is required")
        private VaultEntryType type = VaultEntryType.NOTE;

        private String mediaUrl;

        private String moodEmoji;

        @NotNull(message = "Visibility is required")
        private VaultVisibility visibility = VaultVisibility.PRIVATE;

        private String trustedUsernames;

        private LocalDateTime expiresAt;

        private boolean encrypted = true;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public VaultEntryType getType() { return type; }
        public void setType(VaultEntryType type) { this.type = type; }
        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
        public String getMoodEmoji() { return moodEmoji; }
        public void setMoodEmoji(String moodEmoji) { this.moodEmoji = moodEmoji; }
        public VaultVisibility getVisibility() { return visibility; }
        public void setVisibility(VaultVisibility visibility) { this.visibility = visibility; }
        public String getTrustedUsernames() { return trustedUsernames; }
        public void setTrustedUsernames(String trustedUsernames) { this.trustedUsernames = trustedUsernames; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public boolean isEncrypted() { return encrypted; }
        public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    }

    public static class VaultEntryResponse {
        private Long id;
        private AuthDto.UserSummary owner;
        private String title;
        private String content;
        private VaultEntryType type;
        private String mediaUrl;
        private String moodEmoji;
        private VaultVisibility visibility;
        private String trustedUsernames;
        private LocalDateTime expiresAt;
        private boolean isEncrypted;
        private boolean isExpired;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public VaultEntryResponse() {}

        public VaultEntryResponse(VaultEntry entry) {
            this.id = entry.getId();
            this.owner = new AuthDto.UserSummary(entry.getOwner());
            this.title = entry.getTitle();
            this.content = entry.getContent();
            this.type = entry.getType();
            this.mediaUrl = entry.getMediaUrl();
            this.moodEmoji = entry.getMoodEmoji();
            this.visibility = entry.getVisibility();
            this.trustedUsernames = entry.getTrustedUsernames();
            this.expiresAt = entry.getExpiresAt();
            this.isEncrypted = entry.isEncrypted();
            this.isExpired = (entry.getExpiresAt() != null && entry.getExpiresAt().isBefore(LocalDateTime.now()));
            this.createdAt = entry.getCreatedAt();
            this.updatedAt = entry.getUpdatedAt();
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public AuthDto.UserSummary getOwner() { return owner; }
        public void setOwner(AuthDto.UserSummary owner) { this.owner = owner; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public VaultEntryType getType() { return type; }
        public void setType(VaultEntryType type) { this.type = type; }
        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
        public String getMoodEmoji() { return moodEmoji; }
        public void setMoodEmoji(String moodEmoji) { this.moodEmoji = moodEmoji; }
        public VaultVisibility getVisibility() { return visibility; }
        public void setVisibility(VaultVisibility visibility) { this.visibility = visibility; }
        public String getTrustedUsernames() { return trustedUsernames; }
        public void setTrustedUsernames(String trustedUsernames) { this.trustedUsernames = trustedUsernames; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public boolean isEncrypted() { return isEncrypted; }
        public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }
        public boolean isExpired() { return isExpired; }
        public void setExpired(boolean expired) { isExpired = expired; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class VaultStatsResponse {
        private long totalMemories;
        private long encryptedMemories;
        private long sharedWithTrusted;
        private long openWhenCapsules;

        public VaultStatsResponse(long totalMemories, long encryptedMemories, long sharedWithTrusted, long openWhenCapsules) {
            this.totalMemories = totalMemories;
            this.encryptedMemories = encryptedMemories;
            this.sharedWithTrusted = sharedWithTrusted;
            this.openWhenCapsules = openWhenCapsules;
        }

        public long getTotalMemories() { return totalMemories; }
        public void setTotalMemories(long totalMemories) { this.totalMemories = totalMemories; }
        public long getEncryptedMemories() { return encryptedMemories; }
        public void setEncryptedMemories(long encryptedMemories) { this.encryptedMemories = encryptedMemories; }
        public long getSharedWithTrusted() { return sharedWithTrusted; }
        public void setSharedWithTrusted(long sharedWithTrusted) { this.sharedWithTrusted = sharedWithTrusted; }
        public long getOpenWhenCapsules() { return openWhenCapsules; }
        public void setOpenWhenCapsules(long openWhenCapsules) { this.openWhenCapsules = openWhenCapsules; }
    }
}
