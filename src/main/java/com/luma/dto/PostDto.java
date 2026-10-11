package com.luma.dto;

import com.luma.model.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PostDto {

    public static class CreatePostRequest {
        @Size(max = 2000, message = "Caption must not exceed 2000 characters")
        private String caption;

        private String mediaUrl;

        private String tag; // E.g., #Photography, #Memories, #Life

        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
    }

    public static class CommentRequest {
        @NotBlank(message = "Comment text is required")
        @Size(max = 500)
        private String text;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }

    public static class PostResponse {
        private Long id;
        private AuthDto.UserSummary author;
        private String caption;
        private String mediaUrl;
        private String mediaType;
        private String tag;
        private LocalDateTime createdAt;
        private long likesCount;
        private long commentsCount;
        private boolean likedByCurrentUser;
        private boolean savedByCurrentUser;

        public PostResponse() {}

        public PostResponse(Post post, boolean liked, boolean saved, long likes, long comments) {
            this.id = post.getId();
            this.author = new AuthDto.UserSummary(post.getAuthor());
            this.caption = post.getCaption();
            this.mediaUrl = post.getMediaUrl();
            this.mediaType = post.getMediaType();
            this.tag = post.getTag();
            this.createdAt = post.getCreatedAt();
            this.likedByCurrentUser = liked;
            this.savedByCurrentUser = saved;
            this.likesCount = likes;
            this.commentsCount = comments;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public AuthDto.UserSummary getAuthor() { return author; }
        public void setAuthor(AuthDto.UserSummary author) { this.author = author; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
        public String getMediaType() { return mediaType; }
        public void setMediaType(String mediaType) { this.mediaType = mediaType; }
        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public long getLikesCount() { return likesCount; }
        public void setLikesCount(long likesCount) { this.likesCount = likesCount; }
        public long getCommentsCount() { return commentsCount; }
        public void setCommentsCount(long commentsCount) { this.commentsCount = commentsCount; }
        public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
        public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
        public boolean isSavedByCurrentUser() { return savedByCurrentUser; }
        public void setSavedByCurrentUser(boolean savedByCurrentUser) { this.savedByCurrentUser = savedByCurrentUser; }
    }

    public static class UserProfileResponse {
        private AuthDto.UserSummary user;
        private long postsCount;
        private long followersCount;
        private long followingCount;
        private boolean isFollowing;

        public UserProfileResponse(AuthDto.UserSummary user, long postsCount, long followersCount, long followingCount, boolean isFollowing) {
            this.user = user;
            this.postsCount = postsCount;
            this.followersCount = followersCount;
            this.followingCount = followingCount;
            this.isFollowing = isFollowing;
        }

        public AuthDto.UserSummary getUser() { return user; }
        public void setUser(AuthDto.UserSummary user) { this.user = user; }
        public long getPostsCount() { return postsCount; }
        public void setPostsCount(long postsCount) { this.postsCount = postsCount; }
        public long getFollowersCount() { return followersCount; }
        public void setFollowersCount(long followersCount) { this.followersCount = followersCount; }
        public long getFollowingCount() { return followingCount; }
        public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }
        public boolean isFollowing() { return isFollowing; }
        public void setFollowing(boolean following) { isFollowing = following; }
    }
}
