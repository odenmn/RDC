package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.*;
import com.xjx.example.service.*;
import com.xjx.example.service.impl.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/*")
public class AdminServlet extends BaseServlet {

    private final ReviewService reviewService = new ReviewServiceImpl();
    private final SongService songService = new SongServiceImpl();
    private final AlbumService albumService = new AlbumServiceImpl();
    private final FollowService followService = new FollowServiceImpl();
    private final MessageService messageService = new MessageServiceImpl();

    private Integer getReviewIdFromRequest(HttpServletRequest req) {
        String reviewIdStr = req.getParameter("reviewId");
        if (reviewIdStr == null || reviewIdStr.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(reviewIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private JSONObject setErrorResponse(String message) throws IOException {
        JSONObject json = new JSONObject();
        json.put("success", false);
        json.put("message", message);
        return json;
    }

    private JSONObject setSuccessResponse(String message) throws IOException {
        JSONObject json = new JSONObject();
        json.put("success", true);
        json.put("message", message);
        return json;
    }

    public void getReviewById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Integer reviewId = getReviewIdFromRequest(req);
        JSONObject json;
        if (reviewId == null) {
            json = setErrorResponse("无效的 reviewId 参数");
            resp.getWriter().write(json.toJSONString());
            return;
        }
        Review review = reviewService.getReviewById(reviewId);
        if (review == null) {
            json = setErrorResponse("未找到该审核记录");
            resp.getWriter().write(json.toJSONString());
        }
        json = setSuccessResponse("获取审核记录成功");
        json.put("review", review);
        resp.getWriter().write(json.toJSONString());
    }
    public void getPendingReviews(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        int currentPage = Integer.parseInt(req.getParameter("currentPage"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        PageBean<Review> pageBean = reviewService.getPendingReviewByPage(currentPage, pageSize);
        JSONObject json;
        if (pageBean.getRows() == null || pageBean.getRows().isEmpty()) {
            json = setErrorResponse("没有待审核的记录");
            resp.getWriter().write(json.toJSONString());
            return;
        }
        json = setSuccessResponse("获取待审核记录成功");
        json.put("pageBean", pageBean);
        resp.getWriter().write(json.toJSONString());
    }

    // 处理审核
    public void processingReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Integer reviewId = getReviewIdFromRequest(req);
        System.out.println("reviewId: " + reviewId);
        JSONObject json;
        if (reviewId == null) {
            json = setErrorResponse("无效的 reviewId 参数");
            resp.getWriter().write(json.toJSONString());
            return;
        }
        Review review = reviewService.getReviewById(reviewId);
        boolean success = reviewService.processingReview(review);
        if (success) {
            json = setSuccessResponse("正在处理审核");
            // 发送消息给用户
            Song song = null;
            Album album = null;
            String name;
            if ("歌曲".equals(review.getContent())){
                song = songService.getSongById(review.getUploadWorkId());
                name = song.getTitle();
            } else{
                album = albumService.getAlbumById(review.getUploadWorkId());
                name = album.getTitle();
            }
            User user = (User) req.getSession().getAttribute("user");
            Message message = new Message();
            message.setTitle("审核通知");
            message.setContent(review.getContent() + "《" + name + "》审核中");
            message.setSenderId(user.getId());
            message.setReceiverId(review.getUserId());
            if (messageService.sendMessage(message)) {
                json.put("sendMessage", "审核通过中，已发送消息给用户");
            } else {
                json.put("sendMessage", "审核处理中，但发送消息给用户失败");
            }
        } else {
            json = setErrorResponse("处理审核失败");
        }
        resp.getWriter().write(json.toJSONString());
    }
    // 通过审核
    public void approveReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Integer reviewId = getReviewIdFromRequest(req);
        System.out.println("reviewId: " + reviewId);
        JSONObject json;
        if (reviewId == null) {
            setErrorResponse("无效的 reviewId 参数");
            return;
        }
        boolean success;
        Review review = reviewService.getReviewById(reviewId);
        System.out.println(review.getContent());
        List<Integer> followers = followService.getFollowersByMusicianId(review.getUserId());
        if ("歌曲".equals(review.getContent())){
            Song song = songService.getSongById(review.getUploadWorkId());
            success = reviewService.approveReview(reviewId) && songService.approveSongPublic(song);
            if (success) {
                json = setSuccessResponse("审核通过成功");
                User user = (User) req.getSession().getAttribute("user");
                Message message = new Message();

                // 给关注者发送消息
                for (Integer followerId : followers) {
                    message.setSenderId(user.getId());
                    message.setReceiverId(followerId);
                    message.setContent("您关注的用户" + user.getUsername() + "上传了新歌曲《" + song.getTitle() + "》，快来收听吧");
                    if(!messageService.sendMessage(message)) {
                        json = setErrorResponse("发送给用户" + followerId + "失败");
                        resp.getWriter().write(json.toJSONString());
                        return;
                    }
                }
                // 发送消息给音乐人
                message.setTitle("审核结果通知");
                message.setContent("歌曲《" + song.getTitle() + "》通过审核");
                message.setSenderId(user.getId());
                message.setReceiverId(review.getUserId());
                if (messageService.sendMessage(message)) {
                    json.put("sendMessage", "审核通过成功，已发送消息给音乐人");
                } else {
                    json.put("sendMessage", "审核通过成功，但发送消息给音乐人失败");
                }
            } else {
                json = setErrorResponse("审核通过失败");
            }
            resp.getWriter().write(json.toJSONString());
        }
        else {
            // 设置专辑内歌曲的公开状态
            List<Song> songs = songService.getSongsByAlbumId(review.getUploadWorkId());
            for (Song song : songs) {
                boolean result = songService.approveSongPublic(song);
                if (!result){
                    json = setErrorResponse("审核通过失败");
                    resp.getWriter().write(json.toJSONString());
                    return;
                }
            }
            // 设置专辑的公开状态
            Album album = albumService.getAlbumById(review.getUploadWorkId());
            if (reviewService.approveReview(reviewId) && albumService.approveAlbumPublic(album)) {
                json = setSuccessResponse("审核通过成功");
                User user = (User) req.getSession().getAttribute("user");
                Message message = new Message();

                // 给关注者发送消息
                for (Integer followerId : followers) {
                    message.setSenderId(user.getId());
                    message.setReceiverId(followerId);
                    message.setContent("您关注的用户" + user.getUsername() + "上传了新专辑《" + album.getTitle() + "》，快来收听吧");
                    if(!messageService.sendMessage(message)) {
                        json = setErrorResponse("发送给用户" + followerId + "失败");
                        resp.getWriter().write(json.toJSONString());
                        return;
                    }
                }

                // 发送消息给音乐人
                message.setTitle("审核结果通知");
                message.setContent("专辑" + album.getTitle() +"通过审核");
                message.setSenderId(user.getId());
                message.setReceiverId(review.getUserId());
                if (messageService.sendMessage(message)) {
                    json.put("sendMessage", "审核通过成功，已发送消息给音乐人");
                } else {
                    json.put("sendMessage", "审核通过成功，但发送消息给音乐人失败");
                }
            } else {
                json = setErrorResponse("审核通过失败");
            }
            resp.getWriter().write(json.toJSONString());
        }
    }

    // 拒绝审核
    public void rejectReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Integer reviewId = getReviewIdFromRequest(req);
        JSONObject json;
        if (reviewId == null) {
            json = setErrorResponse("无效的 reviewId 参数");
            resp.getWriter().write(json.toJSONString());
            return;
        }
        Review review = reviewService.getReviewById(reviewId);
        boolean success;
        if ("歌曲".equals(reviewService.getReviewById(reviewId).getContent())){
            Song song = songService.getSongById(review.getUploadWorkId());
            success = reviewService.rejectReview(reviewId);
            if (success) {
                json = setSuccessResponse("审核拒绝成功");
                // 发送消息给用户
                User user = (User) req.getSession().getAttribute("user");
                Message message = new Message();
                message.setTitle("审核结果通知");
                // 设置拒绝原因
                String content = req.getParameter("content");
                message.setContent("歌曲" + song.getTitle() + "未通过审核，拒绝原因：" + content);
                message.setSenderId(user.getId());
                message.setReceiverId(reviewService.getReviewById(reviewId).getUserId());
                if (messageService.sendMessage(message)) {
                    json.put("sendMessage", "审核拒绝成功，已发送消息给用户");
                } else {
                    json.put("sendMessage", "审核拒绝成功，但发送消息给用户失败");
                }
            } else {
                json= setErrorResponse("审核拒绝失败");
            }
            resp.getWriter().write(json.toJSONString());
        } else {
            json = setSuccessResponse("审核拒绝成功");
            Album album = albumService.getAlbumById(review.getUploadWorkId());
            success = reviewService.rejectReview(reviewId);
            if (success) {
                // 发送消息给用户
                User user = (User) req.getSession().getAttribute("user");
                Message message = new Message();
                message.setTitle("审核结果通知");
                // 设置拒绝原因
                String content = req.getParameter("content");
                message.setContent("专辑" + album.getTitle() + "未通过审核，拒绝原因：" + content);
                message.setSenderId(user.getId());
                message.setReceiverId(reviewService.getReviewById(reviewId).getUserId());
            }
            resp.getWriter().write(json.toJSONString());
        }
    }

    // 分页
    public void selectByPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        int currentPage = Integer.parseInt(req.getParameter("currentPage"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        PageBean<Review> pageBean = reviewService.getPendingReviewByPage(currentPage, pageSize);
        JSONObject json = new JSONObject();
        String jsonString = JSON.toJSONString(pageBean);
        resp.getWriter().write(jsonString);
    }
}