package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Playlist;
import com.xjx.example.entity.Song;
import com.xjx.example.entity.User;
import com.xjx.example.service.PlaylistService;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.PlaylistServiceImpl;
import com.xjx.example.service.impl.UserServiceImpl;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/playlist/*")
public class PlaylistServlet extends BaseServlet {
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB 内存阈值
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB 单个文件最大大小
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB 整个请求最大大小
    private final PlaylistService playlistService = new PlaylistServiceImpl();
    private final UserService userService = new UserServiceImpl();

    // 创建歌单
    public void createPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        // 检查请求是否包含 multipart/form-data
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果请求不是 multipart/form-data，则返回错误信息
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"请求不是 multipart/form-data\"}");
            out.flush();
            return;
        }

        // 配置上传设置
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存阈值，超过此值时将文件写入临时目录
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置单个文件最大大小
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置整个请求最大大小
        upload.setSizeMax(MAX_REQUEST_SIZE);

        JSONObject jsonResponse = new JSONObject();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 解析请求中的表单项
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                String playlistName = null; // 存储歌单名
                FileItem fileItem = null; // 存储文件项
                // 遍历表单项
                for (FileItem item : formItems) {
                    // 判断是否为表单项
                    if (item.isFormField()) {
                        playlistName = item.getString("UTF-8");
                    } else {
                        // 如果是文件项，则保存文件
                        fileItem = item;
                    }
                }

                // 检查文件项和歌单名是否都存在
                if (fileItem != null && playlistName != null) {
                    // 获取真实路径
                    String uploadPath = getServletContext().getRealPath("/audio");
                    File uploadDir = new File(uploadPath);

                    // 如果目录不存在，则创建
                    if (!uploadDir.exists()) {
                        boolean created = uploadDir.mkdirs(); // 创建多级目录
                        if (!created) {
                            System.err.println("无法创建上传目录：" + uploadDir.getAbsolutePath());
                            jsonResponse.put("success", false);
                            jsonResponse.put("message", "无法创建上传目录");
                            response.getWriter().write(jsonResponse.toJSONString());
                            return;
                        }
                    }

                    // 获取原始文件名
                    String fileName = fileItem.getName();
                    if (fileName == null || fileName.isEmpty()) {
                        System.err.println("文件名为空");
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "文件名为空");
                        response.getWriter().write(jsonResponse.toJSONString());
                        return;
                    }

                    // 提取文件扩展名
                    String fileExt = "";
                    int dotIndex = fileName.lastIndexOf(".");
                    if (dotIndex > 0) {
                        fileExt = fileName.substring(dotIndex);
                    }

                    // 使用 UUID 避免重复文件名
                    String uniqueFileName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + fileExt;

                    // 构建目标文件对象
                    File storeFile = new File(uploadDir, uniqueFileName);

                    // 写入磁盘
                    try {
                        fileItem.write(storeFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "文件写入失败：" + e.getMessage());
                        response.getWriter().write(jsonResponse.toJSONString());
                        return;
                    }

                    // 构建文件的 URL
                    String coverUrl = "/XuJunxi_CloudMusic/img/" + uniqueFileName;
                    System.out.println("coverUrl:"+ coverUrl);
                    User user = (User) request.getSession().getAttribute("user");
                    int userId = user.getId();
                    // 创建歌单对象
                    Playlist playlist = new Playlist();
                    playlist.setName(playlistName);
                    playlist.setUserId(userId);
                    playlist.setCoverUrl(coverUrl);

                    boolean success = playlistService.createPlaylist(playlist);
                    jsonResponse.put("success", success);
                    jsonResponse.put("message", success ? "创建歌单成功" : "创建歌单失败");
                    response.getWriter().write(jsonResponse.toJSONString());
                } else {
                    // 如果文件不存在，则返回错误信息
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "该文件不存在");
                }
            } else {
                // 如果表单项为空，则返回错误信息
                jsonResponse.put("success", false);
                jsonResponse.put("message", "该文件不存在");
            }
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }

    }

    // 删除歌单
    public void deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        int playlistId = Integer.parseInt(request.getParameter("playlistId"));

        boolean success = playlistService.deletePlaylist(playlistId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", success ? "删除歌单成功" : "删除歌单失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 添加歌曲到歌单
    public void addSongToPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        String jsonStr = jsonBuilder.toString();

        JSONObject jsonObject = JSON.parseObject(jsonStr);
        int playlistId = jsonObject.getIntValue("playlistId");
        int songId = jsonObject.getIntValue("songId");

        boolean exist = playlistService.checkSongInPlaylist(playlistId, songId);
        if (exist) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", false);
            jsonResponse.put("message", "该歌曲已添加到此歌单中");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        boolean success = playlistService.addSongToPlaylist(playlistId, songId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", success ? "添加歌曲成功" : "添加歌曲失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 从歌单移除歌曲
    public void removeSongFromPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        BufferedReader reader = request.getReader();
        JSONObject json = JSONObject.parseObject(reader.readLine());
        int playlistId = json.getIntValue("playlistId");
        int songId = json.getIntValue("songId");

        boolean success = playlistService.removeSongFromPlaylist(playlistId, songId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", success ? "移除歌曲成功" : "移除歌曲失败");
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取用户的所有歌单
    public void getPlaylistsByUserId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        User user = (User) request.getSession().getAttribute("user");
        int userId = user.getId();

        List<Playlist> playlists = playlistService.getPlaylistsByUserId(userId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", playlists != null);
        jsonResponse.put("message", playlists != null ? "获取歌单成功" : "获取歌单失败");
        jsonResponse.put("playlists", playlists);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 获取歌单中的所有歌曲
    public void getSongsInPlaylist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        BufferedReader reader = request.getReader();
        JSONObject json = JSONObject.parseObject(reader.readLine());
        int playlistId = json.getIntValue("playlistId");

        List<Song> songs = playlistService.getSongsInPlaylist(playlistId);
        for (Song song : songs) {
            Integer authorId = song.getAuthorId();
            User author = userService.getUserById(authorId);
            song.setAuthorName(author.getUsername());
        }
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", songs != null);
        jsonResponse.put("message", songs != null ? "获取歌单歌曲成功" : "获取歌单歌曲失败");
        jsonResponse.put("songs", songs);
        response.getWriter().write(jsonResponse.toJSONString());
    }
}