package com.xjx.example.service;

import com.xjx.example.entity.Message;

import java.sql.SQLException;
import java.util.List;

public interface MessageService {
    boolean sendMessage(Message message);
    boolean deleteMessage(int messageId);
    Message getMessageById(int messageId);
    List<Message> getUserMessages(int userId);
    boolean markMessageAsRead(int messageId);
}