package com.luma.service;

import com.luma.dto.PostDto;
import com.luma.model.*;
import com.luma.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private SavedPostRepository savedPostRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public PostDto.PostResponse createPost(User author, PostDto.CreatePostRequest request) {
        Post post = new Post();
        post.setAuthor(author);
        post.setCaption(request.getCaption());
        post.setMediaUrl(request.getMediaUrl());
        post.setTag(request.getTag());
        post.setMediaType((request.getMediaUrl() != null && !request.getMediaUrl().isEmpty()) ? "IMAGE" : "TEXT");

        Post saved = postRepository.save(post);
        return mapToPostResponse(saved, author);
    }

    public List<PostDto.PostResponse> getAllPosts(User currentUser) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .map(p -> mapToPostResponse(p, currentUser))
                .collect(Collectors.toList());
    }

    public List<PostDto.PostResponse> getFollowingFeed(User currentUser) {
        if (currentUser == null) {
            return getAllPosts(null);
        }

        List<Follow> follows = followRepository.findByFollower(currentUser);
        List<User> followingUsers = follows.stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());

        // Include own posts as well
        followingUsers.add(currentUser);

        List<Post> posts = postRepository.findByAuthorInOrderByCreatedAtDesc(followingUsers);
        if (posts.isEmpty()) {
            // Fallback to all posts so feed is never empty
            return getAllPosts(currentUser);
        }

        return posts.stream()
                .map(p -> mapToPostResponse(p, currentUser))
                .collect(Collectors.toList());
    }

    public List<PostDto.PostResponse> getPostsByAuthor(String username, User currentUser) {
        User author = userRepository.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return postRepository.findByAuthorOrderByCreatedAtDesc(author).stream()
                .map(p -> mapToPostResponse(p, currentUser))
                .collect(Collectors.toList());
    }

    public List<PostDto.PostResponse> getSavedPosts(User currentUser) {
        List<SavedPost> saved = savedPostRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return saved.stream()
                .map(s -> mapToPostResponse(s.getPost(), currentUser))
                .collect(Collectors.toList());
    }

    public List<PostDto.PostResponse> searchPosts(String query, User currentUser) {
        return postRepository.searchPosts(query).stream()
                .map(p -> mapToPostResponse(p, currentUser))
                .collect(Collectors.toList());
    }

    public PostDto.PostResponse getPostById(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return mapToPostResponse(post, currentUser);
    }

    @Transactional
    public boolean toggleLike(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (postLikeRepository.existsByPostAndUser(post, user)) {
            postLikeRepository.deleteByPostAndUser(post, user);
            return false; // un-liked
        } else {
            postLikeRepository.save(new PostLike(post, user));

            // Notify author
            notificationService.createNotification(
                    post.getAuthor(),
                    user,
                    "LIKE",
                    "@" + user.getUsername() + " liked your post.",
                    post.getId()
            );
            return true; // liked
        }
    }

    @Transactional
    public boolean toggleSave(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (savedPostRepository.existsByPostAndUser(post, user)) {
            savedPostRepository.deleteByPostAndUser(post, user);
            return false; // un-saved
        } else {
            savedPostRepository.save(new SavedPost(post, user));
            return true; // saved
        }
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Not authorized to delete this post");
        }

        commentRepository.deleteByPost(post);
        postLikeRepository.deleteByPost(post);
        savedPostRepository.deleteByPost(post);
        postRepository.delete(post);
    }

    public PostDto.PostResponse mapToPostResponse(Post post, User currentUser) {
        long likesCount = postLikeRepository.countByPost(post);
        long commentsCount = commentRepository.countByPost(post);

        boolean liked = false;
        boolean saved = false;

        if (currentUser != null) {
            liked = postLikeRepository.existsByPostAndUser(post, currentUser);
            saved = savedPostRepository.existsByPostAndUser(post, currentUser);
        }

        return new PostDto.PostResponse(post, liked, saved, likesCount, commentsCount);
    }
}
