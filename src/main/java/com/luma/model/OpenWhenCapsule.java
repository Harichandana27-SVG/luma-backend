package com.luma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "open_when_capsules")
public class OpenWhenCapsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnoreProperties({"password", "vaultPin", "email"})
    private User creator;

    @NotBlank
    @Size(max = 50)
    private String recipientUsername; // Can be self username or a friend's username

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false)
    private String title; // E.g., "Open When You Graduate", "Open When You Turn 25"

    private String occasionType; // BIRTHDAY, GRADUATION, ANNIVERSARY, MILESTONE, FUTURE_SELF, CUSTOM

    @Column(columnDefinition = "TEXT", nullable = false)
    private String lockedMessage; // Revealed only when unlocked

    private String lockedMediaUrl; // Optional photo or video attachment

    private LocalDateTime unlockDate; // Future timestamp for time-locked capsule

    @Size(max = 250)
    private String unlockHint; // E.g. "Written with love on a quiet December evening"

    private String unlockPasscode; // Optional passcode for instant unlock or override

    @Column(nullable = false)
    private boolean isUnlocked = false;

    private LocalDateTime unlockedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Transient
    private boolean canBeUnlockedNow;

    public OpenWhenCapsule() {}

    public OpenWhenCapsule(User creator, String recipientUsername, String title, String occasionType,
                           String lockedMessage, LocalDateTime unlockDate, String unlockHint) {
        this.creator = creator;
        this.recipientUsername = recipientUsername;
        this.title = title;
        this.occasionType = occasionType;
        this.lockedMessage = lockedMessage;
        this.unlockDate = unlockDate;
        this.unlockHint = unlockHint;
        this.isUnlocked = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOccasionType() {
        return occasionType;
    }

    public void setOccasionType(String occasionType) {
        this.occasionType = occasionType;
    }

    public String getLockedMessage() {
        return lockedMessage;
    }

    public void setLockedMessage(String lockedMessage) {
        this.lockedMessage = lockedMessage;
    }

    public String getLockedMediaUrl() {
        return lockedMediaUrl;
    }

    public void setLockedMediaUrl(String lockedMediaUrl) {
        this.lockedMediaUrl = lockedMediaUrl;
    }

    public LocalDateTime getUnlockDate() {
        return unlockDate;
    }

    public void setUnlockDate(LocalDateTime unlockDate) {
        this.unlockDate = unlockDate;
    }

    public String getUnlockHint() {
        return unlockHint;
    }

    public void setUnlockHint(String unlockHint) {
        this.unlockHint = unlockHint;
    }

    public String getUnlockPasscode() {
        return unlockPasscode;
    }

    public void setUnlockPasscode(String unlockPasscode) {
        this.unlockPasscode = unlockPasscode;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCanBeUnlockedNow() {
        return canBeUnlockedNow;
    }

    public void setCanBeUnlockedNow(boolean canBeUnlockedNow) {
        this.canBeUnlockedNow = canBeUnlockedNow;
    }
}
