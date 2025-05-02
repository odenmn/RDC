package com.xjx.example.service.impl;

import com.xjx.example.dao.MessageDao;
import com.xjx.example.dao.impl.MessageDaoImpl;
import com.xjx.example.entity.Message;
import com.xjx.example.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class MessageServiceImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final MessageDao messageDao = new MessageDaoImpl();

    @Override
    public boolean sendMessage(Message message) {
        try {
            return messageDao.addMessage(message);
        } catch (SQLException e) {
            logger.error("Failed to send message: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteMessage(int messageId) {
        try {
            return messageDao.deleteMessage(messageId);
        } catch (SQLException e) {
            logger.error("Failed to delete message: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Message getMessageById(int messageId) {
        try {
            return messageDao.getMessageById(messageId);
        } catch (SQLException e) {
            logger.error("Failed to get message: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Message> getUserMessages(int userId) {
        try {
            return messageDao.getMessagesByUserId(userId);
        } catch (SQLException e) {
            logger.error("Failed to get user messages: {}", e.getMessage());
            return null;
        }
    }

    public boolean markMessageAsRead(int messageId) {
        try {
            return messageDao.markMessageAsRead(messageId);
        } catch (SQLException e) {
            logger.error("Failed to mark message as read: {}", e.getMessage());
            return false;
        }
    }
}