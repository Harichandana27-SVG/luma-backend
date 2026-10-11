package com.luma.service;

import com.luma.model.Comment;
import com.luma.model.Post;
import com.luma.model.Role;
import com.luma.model.User;
import com.luma.repository.CommentRepository;
import com.luma.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Comment> getCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    public Comment addComment(Long postId, User user, String text) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment(post, user, text.trim());
        Comment saved = commentRepository.save(comment);

        // Notify post author
        notificationService.createNotification(
                post.getAuthor(),
                user,
                "COMMENT",
                "@" + user.getUsername() + " commented: \"" + (text.length() > 40 ? text.substring(0, 40) + "..." : text) + "\"",
                post.getId()
        );

        return saved;
    }

    @Transactional
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId()) &&
            !comment.getPost().getAuthor().getId().equals(user.getId()) &&
            user.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
