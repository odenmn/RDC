package com.xjx.example.dao;

import com.xjx.example.entity.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageDao {
    boolean addMessage(Message message) throws SQLException;
    boolean deleteMessage(int messageId) throws SQLException;
    Message getMessageById(int messageId) throws SQLException;
    List<Message> getMessagesByUserId(int userId) throws SQLException;
    boolean markMessageAsRead(int messageId) throws SQLException;
}