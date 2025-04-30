package com.xjx.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Album;
import com.xjx.example.entity.User;
import com.xjx.example.service.AlbumService;
import com.xjx.example.service.impl.AlbumServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/album/*")
public class AlbumServlet extends BaseServlet{
    private AlbumService albumService = new AlbumServiceImpl();
    public void getAlbumById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        int albumId = Integer.parseInt(request.getParameter("albumId"));
        Album album = albumService.getAlbumById(albumId);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", album != null);
        jsonResponse.put("album", album);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void getAlbumsByAuthorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        int authorId = user.getId();
        List<Album> albums = albumService.getAlbumByAuthorId(authorId);
        JSONObject jsonResponse = new JSONObject();
        if (albums == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "获取当前用户发布的专辑失败");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }
        jsonResponse.put("success", true);
        jsonResponse.put("message", "获取当前用户发布的专辑成功");
        jsonResponse.put("albums", albums);
        response.getWriter().write(jsonResponse.toJSONString());
    }


}
