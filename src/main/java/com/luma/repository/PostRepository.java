package com.luma.repository;

import com.luma.model.Post;
import com.luma.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByAuthorOrderByCreatedAtDesc(User author);

    List<Post> findByAuthorInOrderByCreatedAtDesc(List<User> authors);

    long countByAuthor(User author);

    List<Post> findByTagIgnoreCaseOrderByCreatedAtDesc(String tag);

    @Query("SELECT p FROM Post p WHERE LOWER(p.caption) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.tag) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("query") String query);
}
