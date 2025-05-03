package com.xjx.example.dao;

import java.sql.SQLException;
import java.util.List;

public interface LikeDao {
    boolean addLike(int userId, int songId) throws SQLException;
    boolean removeLike(int userId, int songId) throws SQLException;
    boolean isLiked(int userId, int songId) throws SQLException;
    int getLikeCount(int songId) throws SQLException;
    List<Integer> getLikedSongsByUserId(int userId) throws SQLException;
}