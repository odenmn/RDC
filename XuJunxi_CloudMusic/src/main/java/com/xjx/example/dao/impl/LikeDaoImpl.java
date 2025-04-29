package com.xjx.example.dao.impl;

import com.xjx.example.dao.LikeDao;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeDaoImpl implements LikeDao {

    @Override
    public boolean addLike(int userId, int songId) throws SQLException {
        String sql = "INSERT INTO like_song (user_id, song_id) VALUES (?, ?)";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, userId, songId) > 0;
    }

    @Override
    public boolean removeLike(int userId, int songId) throws SQLException {
        String sql = "DELETE FROM like_song WHERE user_id = ? AND song_id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, userId, songId) > 0;
    }

    @Override
    public boolean isLiked(int userId, int songId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM like_song WHERE user_id = ? AND song_id = ?";
        try (Connection connection = JDBCUtil.getConnection()) {
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, userId, songId);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public int getLikeCount(int songId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM like_song WHERE song_id = ?";
        try (Connection connection = JDBCUtil.getConnection()) {
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, songId);
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}