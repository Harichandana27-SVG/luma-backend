package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.dto.AuthDto;
import com.luma.model.User;
import com.luma.service.AuthService;
import com.luma.service.VaultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private VaultService vaultService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        try {
            AuthDto.AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.ok("Registration successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.AuthResponse>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        try {
            AuthDto.AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthDto.UserSummary>> getCurrentUser() {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.ok(new AuthDto.UserSummary(user)));
    }

    @PostMapping("/vault-pin/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyVaultPin(@Valid @RequestBody AuthDto.VaultPinRequest request, HttpServletRequest httpRequest) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        boolean valid = authService.verifyVaultPin(user, request.getPin());
        String clientIp = httpRequest.getRemoteAddr();

        if (valid) {
            vaultService.recordAuditLog(user, "VAULT_UNLOCKED", "Vault unlocked successfully via PIN authentication", clientIp);
            return ResponseEntity.ok(ApiResponse.ok("Vault unlocked successfully", true));
        } else {
            vaultService.recordAuditLog(user, "VAULT_UNLOCK_FAILED", "Failed vault PIN attempt", clientIp);
            return ResponseEntity.badRequest().body(ApiResponse.error("Incorrect Vault PIN"));
        }
    }

    @PostMapping("/vault-pin/set")
    public ResponseEntity<ApiResponse<String>> setVaultPin(@Valid @RequestBody AuthDto.VaultPinRequest request, HttpServletRequest httpRequest) {
        User user = authService.getCurrentAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        authService.setVaultPin(user, request.getPin());
        vaultService.recordAuditLog(user, "VAULT_PIN_CHANGED", "Vault master PIN updated", httpRequest.getRemoteAddr());

        return ResponseEntity.ok(ApiResponse.ok("Vault PIN set successfully", "PIN updated"));
    }
}
