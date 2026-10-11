package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.dto.VaultDto;
import com.luma.model.User;
import com.luma.model.VaultAuditLog;
import com.luma.model.VaultEntryType;
import com.luma.service.AuthService;
import com.luma.service.VaultService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    @Autowired
    private VaultService vaultService;

    @Autowired
    private AuthService authService;

    @GetMapping("/entries")
    public ResponseEntity<ApiResponse<List<VaultDto.VaultEntryResponse>>> getMyEntries(
            @RequestParam(required = false) VaultEntryType type) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        List<VaultDto.VaultEntryResponse> entries = vaultService.getMyEntries(currentUser, type);
        return ResponseEntity.ok(ApiResponse.ok(entries));
    }

    @PostMapping("/entries")
    public ResponseEntity<ApiResponse<VaultDto.VaultEntryResponse>> createEntry(
            @Valid @RequestBody VaultDto.CreateVaultEntryRequest request,
            HttpServletRequest httpRequest) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        VaultDto.VaultEntryResponse response = vaultService.createEntry(currentUser, request, httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.ok("Vault entry secured", response));
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<ApiResponse<VaultDto.VaultEntryResponse>> getEntry(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        try {
            VaultDto.VaultEntryResponse response = vaultService.getEntryById(id, currentUser, httpRequest.getRemoteAddr());
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEntry(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        try {
            vaultService.deleteEntry(id, currentUser, httpRequest.getRemoteAddr());
            return ResponseEntity.ok(ApiResponse.ok("Vault entry permanently removed", "deleted"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/shared")
    public ResponseEntity<ApiResponse<List<VaultDto.VaultEntryResponse>>> getSharedEntries() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        List<VaultDto.VaultEntryResponse> shared = vaultService.getSharedEntries(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(shared));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<VaultDto.VaultStatsResponse>> getStats() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        VaultDto.VaultStatsResponse stats = vaultService.getVaultStats(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<VaultAuditLog>>> getAuditLogs() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        List<VaultAuditLog> logs = vaultService.getAuditLogs(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }
}
