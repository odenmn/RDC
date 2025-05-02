package com.xjx.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Message;
import com.xjx.example.entity.User;
import com.xjx.example.service.FollowService;
import com.xjx.example.service.MessageService;
import com.xjx.example.service.impl.FollowServiceImpl;
import com.xjx.example.service.impl.MessageServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/message/*")
public class MessageServlet extends BaseServlet {
    private MessageService messageService = new MessageServiceImpl();
    private FollowService  followService = new FollowServiceImpl();
    public void getUserMessages(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        List<Message> messages = messageService.getUserMessages(user.getId());
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", messages != null);
        jsonResponse.put("message", messages != null ? "获取消息成功" : "获取消息失败");
        jsonResponse.put("messages", messages);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void markMessageAsRead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        User user = (User) request.getSession().getAttribute("user");
        BufferedReader reader = request.getReader();
        StringBuilder jsonData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonData.append(line);
        }
        JSONObject json = JSONObject.parseObject(jsonData.toString());
        int messageId = json.getIntValue("messageId");
        if (messageService.markMessageAsRead(messageId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "标记消息为已读成功");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "标记消息为已读失败");
       }
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void deleteMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        User user = (User) request.getSession().getAttribute("user");
        BufferedReader reader = request.getReader();
        StringBuilder jsonData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonData.append(line);
        }
        JSONObject json = JSONObject.parseObject(jsonData.toString());
        int messageId = json.getIntValue("messageId");
        if (messageService.deleteMessage(messageId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "删除消息成功");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "删除消息失败");
        }
        response.getWriter().write(jsonResponse.toJSONString());
    }
}
