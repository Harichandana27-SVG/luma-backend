package com.luma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "vault_entries")
public class VaultEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"password", "vaultPin", "email"})
    private User owner;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaultEntryType type = VaultEntryType.NOTE;

    private String mediaUrl;

    private String moodEmoji; // e.g., 🌟, 💭, 😌, 🌿, 🎯, 🌙

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VaultVisibility visibility = VaultVisibility.PRIVATE;

    // For TRUSTED_CIRCLE: comma-separated usernames, e.g. "sarah_art,alex_dev"
    @Column(length = 500)
    private String trustedUsernames;

    // For TEMPORARY_ACCESS: expiration timestamp
    private LocalDateTime expiresAt;

    private boolean isEncrypted = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public VaultEntry() {}

    public VaultEntry(User owner, String title, String content, VaultEntryType type, VaultVisibility visibility) {
        this.owner = owner;
        this.title = title;
        this.content = content;
        this.type = type;
        this.visibility = visibility;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public VaultEntryType getType() {
        return type;
    }

    public void setType(VaultEntryType type) {
        this.type = type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMoodEmoji() {
        return moodEmoji;
    }

    public void setMoodEmoji(String moodEmoji) {
        this.moodEmoji = moodEmoji;
    }

    public VaultVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(VaultVisibility visibility) {
        this.visibility = visibility;
    }

    public String getTrustedUsernames() {
        return trustedUsernames;
    }

    public void setTrustedUsernames(String trustedUsernames) {
        this.trustedUsernames = trustedUsernames;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
