package com.xjx.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Song;
import com.xjx.example.entity.User;
import com.xjx.example.service.SongService;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.SongServiceImpl;
import com.xjx.example.service.impl.UserServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/song/*")
public class SongServlet extends BaseServlet{
    private final SongService songService = new SongServiceImpl();
    private final UserService userService = new UserServiceImpl();

    // 添加歌曲
    public void addSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();
        JSONObject json = JSONObject.parseObject(reader.readLine());
        Song song = json.toJavaObject(Song.class);

        Integer songId = songService.addSong(song);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", songId != null);
        jsonResponse.put("message", songId != null ? "添加歌曲成功" : "添加歌曲失败");
        jsonResponse.put("songId", songId);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 更新歌曲
    public void updateSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();
        JSONObject json = JSONObject.parseObject(reader.readLine());
        Song song = json.toJavaObject(Song.class);

        boolean result = songService.updateSong(song);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", result);
        jsonResponse.put("message", result ? "更新歌曲成功" : "更新歌曲失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 删除歌曲
    public void deleteSong(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        int songId = Integer.parseInt(request.getParameter("songId"));

        boolean result = songService.deleteSong(songId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", result);
        jsonResponse.put("message", result ? "删除歌曲成功" : "删除歌曲失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取歌曲详情
    public void getSongById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        int songId = Integer.parseInt(request.getParameter("songId"));
        Song song = songService.getSongById(songId);
        int authorId = song.getAuthorId();
        User author = userService.getUserById(authorId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", song != null);
        jsonResponse.put("message", song != null ? "获取歌曲成功" : "获取歌曲失败");
        jsonResponse.put("song", song);
        jsonResponse.put("author", author);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void getSongsByAuthorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        User user = (User) request.getSession().getAttribute("user");
        int authorId = user.getId();
        List<Song> songs = songService.getSongsByAuthorId(authorId);
        JSONObject jsonResponse = new JSONObject();
        if (songs == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "获取作者歌曲失败");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }
        jsonResponse.put("success", true);
        jsonResponse.put("message", "获取作者歌曲成功");
        jsonResponse.put("songs", songs);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void getSongsByAlbumId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        JSONObject json = JSONObject.parseObject(line);
        int albumId = json.getInteger("albumId");
        List<Song> songs = songService.getSongsByAlbumId(albumId);
        JSONObject jsonResponse = new JSONObject();
        if (songs == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "获取专辑歌曲失败");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }
        jsonResponse.put("success", true);
        jsonResponse.put("message", "获取专辑歌曲成功");
        jsonResponse.put("songs", songs);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取作者非专辑歌曲
    public void getNonAlbumSongsByAuthorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        int authorId = user.getId();
        List<Song> songs = songService.getNonAlbumSongsByAuthorId(authorId);
        JSONObject jsonResponse = new JSONObject();
        if (songs == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "获取作者歌曲失败");
            response.getWriter().write(jsonResponse.toJSONString());
        }
        jsonResponse.put("success", true);
        jsonResponse.put("message", "获取作者歌曲成功");
        jsonResponse.put("songs", songs);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 搜索歌曲
    public void searchSongsByTitle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        int currentPage = Integer.parseInt(request.getParameter("currentPage"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        String keyword = request.getParameter("keyword");
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        PageBean<Song> pageBean = songService.searchSongsByTitleWithSort(keyword, currentPage, pageSize, sortBy, order);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", true);
        jsonResponse.put("message", "搜索歌曲成功");
        jsonResponse.put("pageBean", pageBean);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void getRandomRecommendations(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            int count = 5;// 获取5首随机推荐
            List<Song> songs = songService.getRandomRecommendations(count);

            JSONObject jsonResponse = new JSONObject();
            if (songs != null) {
                jsonResponse.put("success", true);
                jsonResponse.put("songs", songs);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "获取推荐失败");
            }
            response.getWriter().write(jsonResponse.toJSONString());

        } catch (NumberFormatException e) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "参数错误");
            response.getWriter().write(jsonResponse.toJSONString());
        }
    }

    public void increasePlayCount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        int songId = Integer.parseInt(request.getParameter("songId"));
        boolean result = songService.increasePlayCount(songId);
        JSONObject jsonResponse = new JSONObject();
        if (result) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "播放次数增加成功");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "播放次数增加失败");
        }
        response.getWriter().write(jsonResponse.toJSONString());
    }
}
