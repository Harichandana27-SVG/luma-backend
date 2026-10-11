package com.luma.service;

import com.luma.dto.AuthDto;
import com.luma.dto.PostDto;
import com.luma.model.Follow;
import com.luma.model.User;
import com.luma.repository.FollowRepository;
import com.luma.repository.PostRepository;
import com.luma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    public PostDto.UserProfileResponse getUserProfile(String username, User currentUser) {
        User user = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        long postsCount = postRepository.countByAuthor(user);
        long followersCount = followRepository.countByFollowing(user);
        long followingCount = followRepository.countByFollower(user);

        boolean isFollowing = false;
        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            isFollowing = followRepository.existsByFollowerAndFollowing(currentUser, user);
        }

        return new PostDto.UserProfileResponse(
                new AuthDto.UserSummary(user),
                postsCount,
                followersCount,
                followingCount,
                isFollowing
        );
    }

    public AuthDto.UserSummary updateProfile(User user, AuthDto.ProfileUpdateRequest request) {
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl().trim());
        }

        User updated = userRepository.save(user);
        return new AuthDto.UserSummary(updated);
    }

    @Transactional
    public boolean followUser(User follower, String usernameToFollow) {
        User target = userRepository.findByUsername(usernameToFollow.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (follower.getId().equals(target.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, target)) {
            return false; // Already following
        }

        followRepository.save(new Follow(follower, target));

        // Create notification
        notificationService.createNotification(
                target,
                follower,
                "FOLLOW",
                "@" + follower.getUsername() + " started following you.",
                follower.getId()
        );

        return true;
    }

    @Transactional
    public boolean unfollowUser(User follower, String usernameToUnfollow) {
        User target = userRepository.findByUsername(usernameToUnfollow.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, target)) {
            followRepository.deleteByFollowerAndFollowing(follower, target);
            return true;
        }
        return false;
    }

    public List<AuthDto.UserSummary> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCaseOrFullNameContainingIgnoreCase(query, query)
                .stream()
                .map(AuthDto.UserSummary::new)
                .collect(Collectors.toList());
    }

    public List<AuthDto.UserSummary> getSuggestedUsers(User currentUser) {
        List<User> all = userRepository.findAll();
        return all.stream()
                .filter(u -> currentUser == null || !u.getId().equals(currentUser.getId()))
                .limit(5)
                .map(AuthDto.UserSummary::new)
                .collect(Collectors.toList());
    }
}
