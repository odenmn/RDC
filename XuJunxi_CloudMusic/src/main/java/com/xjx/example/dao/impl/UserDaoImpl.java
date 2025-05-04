package com.xjx.example.dao.impl;

import com.xjx.example.dao.UserDao;
import com.xjx.example.entity.User;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public boolean addUser(User user) throws SQLException { // 修改为boolean
        String sql = "INSERT INTO user (username, email, password, salt, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(conn, sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getSalt(), user.getRole()) > 0; // 返回执行结果
        }
    }

    @Override
    public User getUserById(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()){
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, id)){
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, username)){
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM user";
        List<User> users = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection()){
             try (ResultSet rs = JDBCUtil.executeQuery(conn, sql)){
                 while (rs.next()) {
                     users.add(mapResultSetToUser(rs));
                 }
             }
        }

        return users;
    }

    @Override
    public boolean updateUser(User user) throws SQLException { // 修改为boolean
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, avatar_url = ?, phone = ?, role = ? WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(conn, sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getAvatar(), user.getPhone(), user.getRole(), user.getId()) > 0; // 返回执行结果
        }
    }

    @Override
    public boolean deleteUser(int id) throws SQLException { // 修改为boolean
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(conn, sql, id) > 0; // 返回执行结果
        }
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, email)) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> searchUsersByUsername(String keyword, int begin, int pageSize) {
        List<User> users = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return users;
        }
        String sql = "SELECT * FROM user WHERE username LIKE ? LIMIT ?, ?";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, "%" + keyword + "%", begin, pageSize)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public int getTotalCountByKeyword(String keyword) {
        String sql = "SELECT COUNT(*) FROM user WHERE username LIKE ?";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, "%" + keyword + "%")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setAvatar(rs.getString("avatar_url"));
        user.setSalt(rs.getString("salt"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        user.setRole(rs.getInt("role"));
        return user;
    }
}