package com.xjx.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Song;
import com.xjx.example.entity.User;
import com.xjx.example.service.LikeService;
import com.xjx.example.service.SongService;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.LikeServiceImpl;
import com.alibaba.fastjson.JSON;
import com.xjx.example.service.impl.SongServiceImpl;
import com.xjx.example.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/like/*")
public class LikeServlet extends BaseServlet {
    private final LikeService likeService = new LikeServiceImpl();
    private final SongService songService = new SongServiceImpl();
    private final UserService userService = new UserServiceImpl();

    // 处理点赞操作
    public void addLike(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        int userId = user.getId();
        int songId = Integer.parseInt(request.getParameter("songId"));
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();

        // 判断是否已经点过赞
        boolean isAlreadyLiked = likeService.isLiked(userId, songId);
        if (isAlreadyLiked) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "您已经点过赞了");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        Song song = songService.getSongById(songId);
        song.setLikeCount(song.getLikeCount() + 1);
        boolean result = likeService.addLike(userId, songId) && songService.updateSong(song);
        jsonResponse.put("success", result);
        jsonResponse.put("message", result ? "点赞成功" : "点赞失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 处理取消点赞操作
    public void removeLike(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        int userId = user.getId();
        int songId = Integer.parseInt(request.getParameter("songId"));
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        // 判断是否已经点过赞
        boolean isAlreadyLiked = likeService.isLiked(userId, songId);
        if (!isAlreadyLiked) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "您还未点过赞");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        Song song = songService.getSongById(songId);
        song.setLikeCount(song.getLikeCount() - 1);
        boolean result = likeService.removeLike(userId, songId) && songService.updateSong(song);
        jsonResponse.put("success", result);
        jsonResponse.put("message", result ? "取消点赞成功" : "取消点赞失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取点赞总数
    public void getLikeCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        int songId = Integer.parseInt(request.getParameter("songId"));
        int count = likeService.getLikeCount(songId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("likeCount", count);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void getLikedSongsByUserId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        int userId = user.getId();
        List<Song> songs = likeService.getLikedSongsByUserId(userId);
        for (Song song : songs) {
            Integer authorId = song.getAuthorId();
            User author = userService.getUserById(authorId);
            song.setAuthorName(author.getUsername());
        }
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", songs != null);
        jsonResponse.put("message", songs != null ? "获取用户点赞歌曲成功" : "获取用户点赞歌曲失败");
        jsonResponse.put("songs", songs);
        response.getWriter().write(jsonResponse.toJSONString());
    }
}