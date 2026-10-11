package com.luma.dto;

import com.luma.model.OpenWhenCapsule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class OpenWhenDto {

    public static class CreateCapsuleRequest {
        private String recipientUsername; // If null/empty, defaults to current user (Self)

        @NotBlank(message = "Title is required (e.g. Open When You Graduate)")
        @Size(max = 150)
        private String title;

        private String occasionType; // BIRTHDAY, GRADUATION, ANNIVERSARY, MILESTONE, FUTURE_SELF, EMOTIONAL_SUPPORT, CUSTOM

        @NotBlank(message = "The locked message cannot be empty")
        private String lockedMessage;

        private String lockedMediaUrl;

        private LocalDateTime unlockDate; // If time-locked

        private String unlockHint;

        private String unlockPasscode; // Optional passcode

        public String getRecipientUsername() { return recipientUsername; }
        public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getOccasionType() { return occasionType; }
        public void setOccasionType(String occasionType) { this.occasionType = occasionType; }
        public String getLockedMessage() { return lockedMessage; }
        public void setLockedMessage(String lockedMessage) { this.lockedMessage = lockedMessage; }
        public String getLockedMediaUrl() { return lockedMediaUrl; }
        public void setLockedMediaUrl(String lockedMediaUrl) { this.lockedMediaUrl = lockedMediaUrl; }
        public LocalDateTime getUnlockDate() { return unlockDate; }
        public void setUnlockDate(LocalDateTime unlockDate) { this.unlockDate = unlockDate; }
        public String getUnlockHint() { return unlockHint; }
        public void setUnlockHint(String unlockHint) { this.unlockHint = unlockHint; }
        public String getUnlockPasscode() { return unlockPasscode; }
        public void setUnlockPasscode(String unlockPasscode) { this.unlockPasscode = unlockPasscode; }
    }

    public static class UnlockCapsuleRequest {
        private String passcode;

        public String getPasscode() { return passcode; }
        public void setPasscode(String passcode) { this.passcode = passcode; }
    }

    public static class CapsuleResponse {
        private Long id;
        private AuthDto.UserSummary creator;
        private String recipientUsername;
        private String title;
        private String occasionType;
        private String lockedMessage;
        private String lockedMediaUrl;
        private LocalDateTime unlockDate;
        private String unlockHint;
        private boolean isUnlocked;
        private boolean canBeUnlockedNow;
        private boolean hasPasscode;
        private LocalDateTime unlockedAt;
        private LocalDateTime createdAt;

        public CapsuleResponse() {}

        public CapsuleResponse(OpenWhenCapsule capsule, boolean revealContent) {
            this.id = capsule.getId();
            this.creator = new AuthDto.UserSummary(capsule.getCreator());
            this.recipientUsername = capsule.getRecipientUsername();
            this.title = capsule.getTitle();
            this.occasionType = capsule.getOccasionType();
            this.unlockDate = capsule.getUnlockDate();
            this.unlockHint = capsule.getUnlockHint();
            this.isUnlocked = capsule.isUnlocked();
            this.unlockedAt = capsule.getUnlockedAt();
            this.createdAt = capsule.getCreatedAt();
            this.hasPasscode = (capsule.getUnlockPasscode() != null && !capsule.getUnlockPasscode().isEmpty());

            boolean timeArrived = (capsule.getUnlockDate() == null || capsule.getUnlockDate().isBefore(LocalDateTime.now()));
            this.canBeUnlockedNow = !capsule.isUnlocked() && timeArrived;

            if (revealContent && capsule.isUnlocked()) {
                this.lockedMessage = capsule.getLockedMessage();
                this.lockedMediaUrl = capsule.getLockedMediaUrl();
            } else {
                this.lockedMessage = null;
                this.lockedMediaUrl = null;
            }
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public AuthDto.UserSummary getCreator() { return creator; }
        public void setCreator(AuthDto.UserSummary creator) { this.creator = creator; }
        public String getRecipientUsername() { return recipientUsername; }
        public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getOccasionType() { return occasionType; }
        public void setOccasionType(String occasionType) { this.occasionType = occasionType; }
        public String getLockedMessage() { return lockedMessage; }
        public void setLockedMessage(String lockedMessage) { this.lockedMessage = lockedMessage; }
        public String getLockedMediaUrl() { return lockedMediaUrl; }
        public void setLockedMediaUrl(String lockedMediaUrl) { this.lockedMediaUrl = lockedMediaUrl; }
        public LocalDateTime getUnlockDate() { return unlockDate; }
        public void setUnlockDate(LocalDateTime unlockDate) { this.unlockDate = unlockDate; }
        public String getUnlockHint() { return unlockHint; }
        public void setUnlockHint(String unlockHint) { this.unlockHint = unlockHint; }
        public boolean isUnlocked() { return isUnlocked; }
        public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
        public boolean isCanBeUnlockedNow() { return canBeUnlockedNow; }
        public void setCanBeUnlockedNow(boolean canBeUnlockedNow) { this.canBeUnlockedNow = canBeUnlockedNow; }
        public boolean isHasPasscode() { return hasPasscode; }
        public void setHasPasscode(boolean hasPasscode) { this.hasPasscode = hasPasscode; }
        public LocalDateTime getUnlockedAt() { return unlockedAt; }
        public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
