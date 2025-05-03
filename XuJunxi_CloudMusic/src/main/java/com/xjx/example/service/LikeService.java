package com.xjx.example.service;

import com.xjx.example.entity.Song;

import java.util.List;

public interface LikeService {
    boolean addLike(int userId, int songId);
    boolean removeLike(int userId, int songId);
    boolean isLiked(int userId, int songId);
    int getLikeCount(int songId);
    List<Song> getLikedSongsByUserId(int userId);
}