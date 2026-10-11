package com.luma.dto;

import com.luma.model.Role;
import com.luma.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDto {

    public static class LoginRequest {
        @NotBlank(message = "Username or email is required")
        private String usernameOrEmail;

        @NotBlank(message = "Password is required")
        private String password;

        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30)
        private String username;

        @NotBlank(message = "Email is required")
        @Email
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50)
        private String password;

        @NotBlank(message = "Full name is required")
        private String fullName;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class AuthResponse {
        private String token;
        private String tokenType = "Bearer";
        private UserSummary user;

        public AuthResponse(String token, UserSummary user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        public UserSummary getUser() { return user; }
        public void setUser(UserSummary user) { this.user = user; }
    }

    public static class UserSummary {
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String bio;
        private String avatarUrl;
        private Role role;
        private boolean hasVaultPin;

        public UserSummary() {}

        public UserSummary(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.bio = user.getBio();
            this.avatarUrl = user.getAvatarUrl();
            this.role = user.getRole();
            this.hasVaultPin = (user.getVaultPin() != null && !user.getVaultPin().isEmpty());
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public boolean isHasVaultPin() { return hasVaultPin; }
        public void setHasVaultPin(boolean hasVaultPin) { this.hasVaultPin = hasVaultPin; }
    }

    public static class ProfileUpdateRequest {
        private String fullName;
        private String bio;
        private String avatarUrl;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }

    public static class VaultPinRequest {
        @NotBlank(message = "PIN is required")
        private String pin;

        public String getPin() { return pin; }
        public void setPin(String pin) { this.pin = pin; }
    }
}
