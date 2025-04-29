package com.xjx.example.dao.impl;

import com.xjx.example.dao.FollowDao;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowDaoImpl implements FollowDao {

    @Override
    public boolean addFollow(int followerId, int followeeId) throws SQLException {
        String sql = "INSERT INTO follow (follower_id, followee_id) VALUES (?, ?)";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, followerId, followeeId) > 0;
    }

    @Override
    public boolean removeFollow(int followerId, int followeeId) throws SQLException {
        String sql = "DELETE FROM follow WHERE follower_id = ? AND followee_id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, followerId, followeeId) > 0;
    }

    @Override
    public boolean isFollowing(int followerId, int followeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM follow WHERE follower_id = ? AND followee_id = ?";
        try (Connection connection = JDBCUtil.getConnection()) {
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, followerId, followeeId);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public int getFollowerCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM follow WHERE followee_id = ?";
        try (Connection connection = JDBCUtil.getConnection()) {
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, userId);
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int getFollowingCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ?";
        try (Connection connection = JDBCUtil.getConnection()) {
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, userId);
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}