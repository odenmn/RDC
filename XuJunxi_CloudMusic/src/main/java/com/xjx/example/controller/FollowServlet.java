package com.xjx.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Song;
import com.xjx.example.entity.User;
import com.xjx.example.service.FollowService;
import com.xjx.example.service.SongService;
import com.xjx.example.service.impl.FollowServiceImpl;
import com.xjx.example.service.impl.SongServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/follow/*")
public class FollowServlet extends BaseServlet{
    private final FollowService followService = new FollowServiceImpl();
    private final SongService songService = new SongServiceImpl();

    // 处理添加关注操作
    public void addFollow(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        User user = (User) request.getSession().getAttribute("user");
        int followerId = user.getId();
        String songIdStr = request.getParameter("songId");
        String followerIdStr = request.getParameter("userId");
        int followeeId;
        // 直接搜索到用户，关注用户
        if (songIdStr == null && followerIdStr != null){
            followeeId = Integer.parseInt(followerIdStr);
            if (followerId == followeeId){
                jsonResponse.put("success", false);
                jsonResponse.put("message", "不能关注自己");
                response.getWriter().write(jsonResponse.toJSONString());
                return;
            }
            // 判断是否已经关注
            boolean isAlreadyFollowing = followService.isFollowing(followerId, followeeId);
            if (isAlreadyFollowing) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "您已经关注了该用户");
                response.getWriter().write(jsonResponse.toJSONString());
                return;
            }

            boolean result = followService.addFollow(followerId, followeeId);
            jsonResponse.put("success", result);
            jsonResponse.put("message", result ? "关注成功" : "关注失败");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        // 从歌曲中关注用户
        int songId = Integer.parseInt(songIdStr);
        Song song = songService.getSongById(songId);
        followeeId = song.getAuthorId();

        if (followerId == followeeId){
            jsonResponse.put("success", false);
            jsonResponse.put("message", "不能关注自己");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }
        // 判断是否已经关注
        boolean isAlreadyFollowing = followService.isFollowing(followerId, followeeId);
        if (isAlreadyFollowing) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "您已经关注了该用户");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        boolean result = followService.addFollow(followerId, followeeId);
        jsonResponse.put("success", result);
        jsonResponse.put("message", result ? "关注成功" : "关注失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 处理取消关注操作
    public void removeFollow(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        User user = (User) request.getSession().getAttribute("user");
        int followerId = user.getId();
        String songIdStr = request.getParameter("songId");
        String followerIdStr = request.getParameter("userId");
        int followeeId;
        if (songIdStr == null && followerIdStr != null){
            followeeId = Integer.parseInt(followerIdStr);
            if (followerId == followeeId){
                jsonResponse.put("success", false);
                jsonResponse.put("message", "不能关注自己");
                response.getWriter().write(jsonResponse.toJSONString());
                return;
            }
            // 判断是否已经关注
            boolean isAlreadyFollowing = followService.isFollowing(followerId, followeeId);
            if (!isAlreadyFollowing) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "您还未关注该用户");
                response.getWriter().write(jsonResponse.toJSONString());
                return;
            }

            boolean result = followService.removeFollow(followerId, followeeId);
            jsonResponse.put("success", result);
            jsonResponse.put("message", result ? "取消关注成功" : "取消关注失败");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        int songId = Integer.parseInt(songIdStr);
        Song song = songService.getSongById(songId);
        followeeId = song.getAuthorId();

        // 判断是否已经关注
        boolean isAlreadyFollowing = followService.isFollowing(followerId, followeeId);
        if (!isAlreadyFollowing) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "您还未关注该用户");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        boolean result = followService.removeFollow(followerId, followeeId);
        jsonResponse.put("success", result);
        jsonResponse.put("message", result ? "取消关注成功" : "取消关注失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 检查关注状态
    public void isFollowing(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        int followerId = user.getId();
        int followeeId = Integer.parseInt(request.getParameter("followeeId"));
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        boolean result = followService.isFollowing(followerId, followeeId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("isFollowing", result);
        jsonResponse.put("message", result ? "已关注" : "未关注");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取粉丝数
    public void getFollowerCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        int count = followService.getFollowerCount(userId);
        System.out.println("粉丝数：" + count);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("followerCount", count);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取关注数
    public void getFollowingCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        int userId = user.getId();
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        int count = followService.getFollowingCount(userId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("followingCount", count);
        response.getWriter().write(jsonResponse.toJSONString());
    }
}
