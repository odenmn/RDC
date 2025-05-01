package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Album;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Review;
import com.xjx.example.entity.Song;
import com.xjx.example.service.AlbumService;
import com.xjx.example.service.ReviewService;
import com.xjx.example.service.SongService;
import com.xjx.example.service.impl.AlbumServiceImpl;
import com.xjx.example.service.impl.ReviewServiceImpl;
import com.xjx.example.service.impl.SongServiceImpl;

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
//        json.put("pendingReviews", pendingReviews);
        json.put("pageBean", pageBean);
        resp.getWriter().write(json.toJSONString());
    }

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
        } else {
            json = setErrorResponse("处理审核失败");
        }
        resp.getWriter().write(json.toJSONString());
    }
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
        if ("歌曲".equals(review.getContent())){
            Song song = songService.getSongById(review.getUploadWorkId());
            success = reviewService.approveReview(reviewId) && songService.approveSongPublic(song);
            if (success) {
                json = setSuccessResponse("审核通过成功");
                resp.getWriter().write(json.toJSONString());
            } else {
                json = setErrorResponse("审核通过失败");
                resp.getWriter().write(json.toJSONString());
            }
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
                resp.getWriter().write(json.toJSONString());
            } else {
                json = setErrorResponse("审核通过失败");
                resp.getWriter().write(json.toJSONString());
            }
        }
    }

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
        boolean success;
        if ("歌曲".equals(reviewService.getReviewById(reviewId).getContent())){
            Song song = songService.getSongById(reviewService.getReviewById(reviewId).getUploadWorkId());
            success = reviewService.rejectReview(reviewId) && songService.rejectSongPublic(song);
            if (success) {
                json = setSuccessResponse("审核拒绝成功");
                resp.getWriter().write(json.toJSONString());
            } else {
                json= setErrorResponse("审核拒绝失败");
                resp.getWriter().write(json.toJSONString());
            }
        } else {
            json = setSuccessResponse("审核拒绝成功");
            resp.getWriter().write(json.toJSONString());
        }
    }

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