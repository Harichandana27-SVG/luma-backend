package com.luma.service;

import com.luma.config.JwtTokenProvider;
import com.luma.dto.AuthDto;
import com.luma.model.Role;
import com.luma.model.User;
import com.luma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim().toLowerCase());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName().trim());
        user.setRole(Role.ROLE_USER);
        user.setBio("Exploring LUMA ✨");
        user.setAvatarUrl("https://api.dicebear.com/7.x/bottts/svg?seed=" + user.getUsername());
        // Default vault pin: 1234
        user.setVaultPin(passwordEncoder.encode("1234"));

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser.getUsername());

        return new AuthDto.AuthResponse(token, new AuthDto.UserSummary(savedUser));
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        String identifier = request.getUsernameOrEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier);
        }

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOpt.get();
        String token = tokenProvider.generateToken(user.getUsername());

        return new AuthDto.AuthResponse(token, new AuthDto.UserSummary(user));
    }

    public User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        if (auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }

        String username = auth.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean verifyVaultPin(User user, String rawPin) {
        if (user.getVaultPin() == null) {
            // If not set yet, compare to default "1234"
            return "1234".equals(rawPin);
        }
        return passwordEncoder.matches(rawPin, user.getVaultPin());
    }

    public void setVaultPin(User user, String newPin) {
        user.setVaultPin(passwordEncoder.encode(newPin));
        userRepository.save(user);
    }
}
