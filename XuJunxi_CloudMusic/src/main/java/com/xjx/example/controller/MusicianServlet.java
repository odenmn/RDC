package com.xjx.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Review;
import com.xjx.example.entity.Song;
import com.xjx.example.entity.User;
import com.xjx.example.service.AlbumService;
import com.xjx.example.service.ReviewService;
import com.xjx.example.service.SongService;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.AlbumServiceImpl;
import com.xjx.example.service.impl.ReviewServiceImpl;
import com.xjx.example.service.impl.SongServiceImpl;
import com.xjx.example.service.impl.UserServiceImpl;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/musician/*")
public class MusicianServlet extends BaseServlet {
    private static final String UPLOAD_DIRECTORY = "uploads"; // 上传文件的目录
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB 内存阈值
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB 单个文件最大大小
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB 整个请求最大大小

    private final UserService userService = new UserServiceImpl();;
    private final SongService songService = new SongServiceImpl();
    private final AlbumService albumService = new AlbumServiceImpl();
    private final ReviewService reviewService = new ReviewServiceImpl();
    /**
     * 认证为音乐人
     *
     * @param request 用于获取请求信息的HttpServletRequest对象
     * @param response 用于向客户端发送响应的HttpServletResponse对象
     * @throws ServletException 如果Servlet遇到异常
     * @throws IOException 如果发生输入或输出异常
     */
    public void becomeMusician(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        boolean success = userService.becomeMusician(user);
        JSONObject jsonResponse = new JSONObject();
        // 设置响应的字符编码和内容类型
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (success) {
            user.setRole(1);
            request.getSession().setAttribute("user", user);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "认证成功");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "认证失败");
        }
        response.getWriter().write(jsonResponse.toJSONString());
    }

    /**
     * 检查用户是否为音乐人
     *
     * @param request 用于获取请求信息的HttpServletRequest对象
     * @param response 用于向客户端发送响应的HttpServletResponse对象
     * @throws ServletException 如果Servlet遇到异常
     * @throws IOException 如果发生输入或输出异常
     */
    public void checkMusicianStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        int role = user.getRole();
        JSONObject jsonResponse = new JSONObject();
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (role == 1) {
            jsonResponse.put("isMusician", 1);
        } else {
            jsonResponse.put("isMusician", 0);
        }
        response.getWriter().write(jsonResponse.toJSONString());
    }

    // 上传歌曲
    public void uploadSong(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // 检查请求是否包含 multipart/form-data
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果请求不是 multipart/form-data，则返回错误信息
            response.setContentType("application/json");
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
                String songTitle = null; // 存储歌曲标题
                FileItem fileItem = null; // 存储文件项
                String genre = null;
                String album = null;
                // 遍历表单项
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        // 如果是文件项，则赋值给 fileItem
                        fileItem = item;
                    } else  {
                        // 如果是表单字段，则根据字段名赋值
                        String fieldName = item.getFieldName();
                        if ("title".equals(fieldName)) {
                            songTitle = item.getString("UTF-8");
                            System.out.println("title:" + songTitle);
                        } else if ("genre".equals(fieldName)) {
                            genre = item.getString("UTF-8");
                            System.out.println("genre:" + genre);
                        } else if ("album".equals(fieldName)) {
                            album = item.getString("UTF-8");
                            System.out.println("album:" + album);
                        }
                    }
                }

                // 检查文件项和标题是否都存在
                if (fileItem != null && songTitle != null) {
                    // 获取文件名
                    String fileName = new File(fileItem.getName()).getName();
                    // 构建文件的 URL
                    String audioUrl = "/XuJunxi_CloudMusic/audio/" + fileName;
                    System.out.println("audioUrl:"+audioUrl);
                    User user = (User) request.getSession().getAttribute("user");
                    // 设置响应的字符编码和内容类型
                    response.setContentType("application/json;charset=UTF-8");
                    response.setCharacterEncoding("UTF-8");

                    // 设置歌曲对象
                    Song song = new Song();
                    if (!album.equals("null")) {
                        Integer albumId = albumService.getAlbumByTitle(album).getId();
                        song.setAlbumId(albumId);
                    }
                    song.setTitle(songTitle);
                    song.setAuthorId(user.getId());
                    song.setGenre(genre);
                    song.setAudioUrl(audioUrl);

                    int songId = songService.addSong(song);
                    if (songId != 0){
                        // 设置审核
                        Review review = new Review();
                        review.setUserId(user.getId());
                        review.setUploadWorkId(songId);
                        review.setContent("歌曲");
                        System.out.println("提交审核");
                        System.out.println("id:"+review.getUploadWorkId());
                        // 提交审核
                        reviewService.submitReview(review);

                        jsonResponse.put("success", true);
                        jsonResponse.put("message", "歌曲《" + songTitle + "》上传成功");
                    } else {
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "歌曲《" + songTitle + "》上传失败");
                    }
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}