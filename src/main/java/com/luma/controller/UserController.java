package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.dto.AuthDto;
import com.luma.dto.PostDto;
import com.luma.model.User;
import com.luma.service.AuthService;
import com.luma.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<PostDto.UserProfileResponse>> getUserProfile(@PathVariable String username) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        try {
            PostDto.UserProfileResponse profile = userService.getUserProfile(username, currentUser);
            return ResponseEntity.ok(ApiResponse.ok(profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<AuthDto.UserSummary>> updateProfile(@RequestBody AuthDto.ProfileUpdateRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        AuthDto.UserSummary updated = userService.updateProfile(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<ApiResponse<Boolean>> followUser(@PathVariable String username) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        try {
            boolean followed = userService.followUser(currentUser, username);
            return ResponseEntity.ok(ApiResponse.ok(followed ? "Followed @" + username : "Already following", followed));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<ApiResponse<Boolean>> unfollowUser(@PathVariable String username) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        try {
            boolean unfollowed = userService.unfollowUser(currentUser, username);
            return ResponseEntity.ok(ApiResponse.ok("Unfollowed @" + username, unfollowed));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AuthDto.UserSummary>>> searchUsers(@RequestParam String q) {
        List<AuthDto.UserSummary> list = userService.searchUsers(q);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/suggested")
    public ResponseEntity<ApiResponse<List<AuthDto.UserSummary>>> getSuggestedUsers() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        List<AuthDto.UserSummary> list = userService.getSuggestedUsers(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
