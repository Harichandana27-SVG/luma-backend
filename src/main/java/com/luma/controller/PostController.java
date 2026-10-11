package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.dto.PostDto;
import com.luma.model.User;
import com.luma.service.AuthService;
import com.luma.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostDto.PostResponse>>> getPosts(
            @RequestParam(required = false, defaultValue = "all") String feed,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String query) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        List<PostDto.PostResponse> posts;

        if (query != null && !query.trim().isEmpty()) {
            posts = postService.searchPosts(query.trim(), currentUser);
        } else if ("following".equalsIgnoreCase(feed)) {
            posts = postService.getFollowingFeed(currentUser);
        } else {
            posts = postService.getAllPosts(currentUser);
        }

        if (tag != null && !tag.trim().isEmpty()) {
            String searchTag = tag.trim().toLowerCase();
            posts = posts.stream()
                    .filter(p -> p.getTag() != null && p.getTag().toLowerCase().contains(searchTag))
                    .toList();
        }

        return ResponseEntity.ok(ApiResponse.ok(posts));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDto.PostResponse>> createPost(@Valid @RequestBody PostDto.CreatePostRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        PostDto.PostResponse created = postService.createPost(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok("Post published successfully", created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDto.PostResponse>> getPost(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        try {
            PostDto.PostResponse post = postService.getPostById(id, currentUser);
            return ResponseEntity.ok(ApiResponse.ok(post));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        try {
            postService.deletePost(id, currentUser);
            return ResponseEntity.ok(ApiResponse.ok("Post deleted successfully", "deleted"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        try {
            boolean liked = postService.toggleLike(id, currentUser);
            return ResponseEntity.ok(ApiResponse.ok(liked ? "Liked post" : "Unliked post", liked));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<ApiResponse<Boolean>> toggleSave(@PathVariable Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        try {
            boolean saved = postService.toggleSave(id, currentUser);
            return ResponseEntity.ok(ApiResponse.ok(saved ? "Post saved to bookmarks" : "Post removed from bookmarks", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<List<PostDto.PostResponse>>> getSavedPosts() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        List<PostDto.PostResponse> list = postService.getSavedPosts(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<ApiResponse<List<PostDto.PostResponse>>> getUserPosts(@PathVariable String username) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        try {
            List<PostDto.PostResponse> list = postService.getPostsByAuthor(username, currentUser);
            return ResponseEntity.ok(ApiResponse.ok(list));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
