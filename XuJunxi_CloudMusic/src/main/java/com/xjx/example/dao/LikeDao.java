package com.xjx.example.dao;

import java.sql.SQLException;

public interface LikeDao {
    boolean addLike(int userId, int songId) throws SQLException;
    boolean removeLike(int userId, int songId) throws SQLException;
    boolean isLiked(int userId, int songId) throws SQLException;
    int getLikeCount(int songId) throws SQLException;
}