package com.luma.controller;

import com.luma.dto.ApiResponse;
import com.luma.dto.PostDto;
import com.luma.model.Comment;
import com.luma.model.User;
import com.luma.service.AuthService;
import com.luma.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthService authService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsForPost(postId);
            return ResponseEntity.ok(ApiResponse.ok(comments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody PostDto.CommentRequest request) {

        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        try {
            Comment comment = commentService.addComment(postId, currentUser, request.getText());
            return ResponseEntity.ok(ApiResponse.ok("Comment added", comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable Long commentId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        try {
            commentService.deleteComment(commentId, currentUser);
            return ResponseEntity.ok(ApiResponse.ok("Comment deleted", "deleted"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
