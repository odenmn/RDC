package com.xjx.example.dao.impl;

import com.xjx.example.dao.MessageDao;
import com.xjx.example.entity.Message;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl implements MessageDao {

    @Override
    public boolean addMessage(Message message) throws SQLException {
        String sql = "INSERT INTO message (sender_id, receiver_id, title, content) VALUES (?, ?, ?, ?)";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql,
                message.getSenderId(), message.getReceiverId(), message.getTitle(), message.getContent()) > 0;
    }

    @Override
    public boolean deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM message WHERE id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, messageId) > 0;
    }

    @Override
    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT * FROM message WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, messageId)) {
            if (rs.next()) {
                return mapResultSetToMessage(rs);
            }
        }
        return null;
    }

    @Override
    public List<Message> getMessagesByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM message WHERE receiver_id = ? ORDER BY created_at DESC";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, userId)) {
            List<Message> messages = new ArrayList<>();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
            return messages;
        }
    }

    @Override
    public boolean markMessageAsRead(int messageId) throws SQLException {
        String sql = "UPDATE message SET is_read = true WHERE id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, messageId) > 0;
    }

    public Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setTitle(rs.getString("title"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        message.setRead(rs.getBoolean("is_read"));
        return message;
    }
}