package com.example.dingpointcontract;

public class Post {
    private String username;
    private String postTime;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private int avatarResId; // 头像资源ID

    public Post(String username, String postTime, String content, String imageUrl,
                int likeCount, int commentCount, int shareCount, int avatarResId) {
        this.username = username;
        this.postTime = postTime;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.avatarResId = avatarResId;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPostTime() { return postTime; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
    public int getShareCount() { return shareCount; }
    public int getAvatarResId() { return avatarResId; }
}