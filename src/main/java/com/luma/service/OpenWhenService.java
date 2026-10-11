package com.luma.service;

import com.luma.dto.OpenWhenDto;
import com.luma.model.OpenWhenCapsule;
import com.luma.model.User;
import com.luma.repository.OpenWhenCapsuleRepository;
import com.luma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenWhenService {

    @Autowired
    private OpenWhenCapsuleRepository capsuleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultService vaultService;

    @Autowired
    private NotificationService notificationService;

    public OpenWhenDto.CapsuleResponse createCapsule(User creator, OpenWhenDto.CreateCapsuleRequest request, String ipAddress) {
        String recipient = request.getRecipientUsername();
        if (recipient == null || recipient.trim().isEmpty()) {
            recipient = creator.getUsername();
        } else {
            recipient = recipient.trim().toLowerCase().replace("@", "");
        }

        OpenWhenCapsule capsule = new OpenWhenCapsule();
        capsule.setCreator(creator);
        capsule.setRecipientUsername(recipient);
        capsule.setTitle(request.getTitle());
        capsule.setOccasionType(request.getOccasionType() != null ? request.getOccasionType() : "CUSTOM");
        capsule.setLockedMessage(request.getLockedMessage());
        capsule.setLockedMediaUrl(request.getLockedMediaUrl());
        capsule.setUnlockDate(request.getUnlockDate());
        capsule.setUnlockHint(request.getUnlockHint());
        capsule.setUnlockPasscode(request.getUnlockPasscode());
        capsule.setUnlocked(false);

        OpenWhenCapsule saved = capsuleRepository.save(capsule);

        vaultService.recordAuditLog(
                creator,
                "CAPSULE_CREATED",
                "Created Open When capsule: \"" + saved.getTitle() + "\" for @" + saved.getRecipientUsername(),
                ipAddress
        );

        // If for another user, send them a teaser notification
        if (!recipient.equalsIgnoreCase(creator.getUsername())) {
            userRepository.findByUsername(recipient).ifPresent(target -> {
                notificationService.createNotification(
                        target,
                        creator,
                        "CAPSULE_RECEIVED",
                        "@" + creator.getUsername() + " left you a locked digital memory: \"" + saved.getTitle() + "\"",
                        saved.getId()
                );
            });
        }

        return new OpenWhenDto.CapsuleResponse(saved, false);
    }

    public List<OpenWhenDto.CapsuleResponse> getMyCapsules(User user) {
        return capsuleRepository.findByCreatorOrderByCreatedAtDesc(user)
                .stream()
                .map(c -> new OpenWhenDto.CapsuleResponse(c, c.isUnlocked()))
                .collect(Collectors.toList());
    }

    public List<OpenWhenDto.CapsuleResponse> getReceivedCapsules(User user) {
        return capsuleRepository.findReceivedCapsules(user.getUsername(), user)
                .stream()
                .map(c -> new OpenWhenDto.CapsuleResponse(c, c.isUnlocked()))
                .collect(Collectors.toList());
    }

    @Transactional
    public OpenWhenDto.CapsuleResponse unlockCapsule(Long id, User user, String providedPasscode, String ipAddress) {
        OpenWhenCapsule capsule = capsuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Capsule not found"));

        boolean isCreator = capsule.getCreator().getId().equals(user.getId());
        boolean isRecipient = capsule.getRecipientUsername().equalsIgnoreCase(user.getUsername());

        if (!isCreator && !isRecipient) {
            throw new SecurityException("You do not have permission to unlock this capsule.");
        }

        if (capsule.isUnlocked()) {
            return new OpenWhenDto.CapsuleResponse(capsule, true);
        }

        boolean timeArrived = (capsule.getUnlockDate() == null || capsule.getUnlockDate().isBefore(LocalDateTime.now()));
        boolean passcodeMatches = false;

        if (capsule.getUnlockPasscode() != null && !capsule.getUnlockPasscode().trim().isEmpty()) {
            passcodeMatches = capsule.getUnlockPasscode().trim().equals(providedPasscode != null ? providedPasscode.trim() : "");
        }

        // Capsule can be unlocked if time has arrived OR correct passcode is provided
        if (!timeArrived && !passcodeMatches) {
            String msg = "This memory is locked until: " + (capsule.getUnlockDate() != null ? capsule.getUnlockDate() : "a future date");
            if (capsule.getUnlockPasscode() != null && !capsule.getUnlockPasscode().isEmpty()) {
                msg += ". Passcode incorrect or not provided.";
            }
            throw new IllegalStateException(msg);
        }

        capsule.setUnlocked(true);
        capsule.setUnlockedAt(LocalDateTime.now());
        OpenWhenCapsule saved = capsuleRepository.save(capsule);

        vaultService.recordAuditLog(
                user,
                "CAPSULE_UNLOCKED",
                "Unlocked Open When capsule: \"" + saved.getTitle() + "\"",
                ipAddress
        );

        // If unlocked by recipient, notify creator
        if (isRecipient && !isCreator) {
            notificationService.createNotification(
                    capsule.getCreator(),
                    user,
                    "CAPSULE_UNLOCKED",
                    "@" + user.getUsername() + " unlocked your capsule: \"" + saved.getTitle() + "\"",
                    saved.getId()
            );
        }

        return new OpenWhenDto.CapsuleResponse(saved, true);
    }
}
