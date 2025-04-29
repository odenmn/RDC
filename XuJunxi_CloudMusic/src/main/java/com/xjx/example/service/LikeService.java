package com.xjx.example.service;

public interface LikeService {
    boolean addLike(int userId, int songId);
    boolean removeLike(int userId, int songId);
    boolean isLiked(int userId, int songId);
    int getLikeCount(int songId);
}