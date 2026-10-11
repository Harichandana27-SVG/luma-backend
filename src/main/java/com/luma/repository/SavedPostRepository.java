package com.luma.repository;

import com.luma.model.Post;
import com.luma.model.SavedPost;
import com.luma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPost, Long> {
    List<SavedPost> findByUserOrderByCreatedAtDesc(User user);
    Optional<SavedPost> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
    void deleteByPost(Post post);
}
