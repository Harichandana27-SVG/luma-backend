package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.dto.OpenWhenDto;
import com.luma.model.User;
import com.luma.service.AuthService;
import com.luma.service.OpenWhenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/open-when")
public class OpenWhenController {

    @Autowired
    private OpenWhenService openWhenService;

    @Autowired
    private AuthService authService;

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OpenWhenDto.CapsuleResponse>>> getMyCapsules() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        List<OpenWhenDto.CapsuleResponse> list = openWhenService.getMyCapsules(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<OpenWhenDto.CapsuleResponse>>> getReceivedCapsules() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        List<OpenWhenDto.CapsuleResponse> list = openWhenService.getReceivedCapsules(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OpenWhenDto.CapsuleResponse>> createCapsule(
            @Valid @RequestBody OpenWhenDto.CreateCapsuleRequest request,
            HttpServletRequest httpRequest) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        OpenWhenDto.CapsuleResponse response = openWhenService.createCapsule(currentUser, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.ok("Digital time capsule sealed", response));
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<OpenWhenDto.CapsuleResponse>> unlockCapsule(
            @PathVariable Long id,
            @RequestBody(required = false) OpenWhenDto.UnlockCapsuleRequest request,
            HttpServletRequest httpRequest) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        String passcode = (request != null) ? request.getPasscode() : null;

        try {
            OpenWhenDto.CapsuleResponse unlocked = openWhenService.unlockCapsule(id, currentUser, passcode, httpRequest.getRemoteAddr());
            return ResponseEntity.ok(ApiResponse.ok("Capsule unlocked! Memory revealed.", unlocked));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
