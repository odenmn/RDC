package com.xjx.example.dao.impl;

import com.xjx.example.dao.ReviewDao;
import com.xjx.example.entity.Review;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReviewDaoImpl implements ReviewDao {
    @Override
    public boolean insertReview(Review review) throws Exception {
        String sql = "INSERT INTO review (user_id, uploadwork_id, content) VALUES (?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(connection, sql, review.getUserId(), review.getUploadWorkId(), review.getContent()) > 0;
        }
    }

    @Override
    public boolean updateReviewStatus(Review review) throws Exception {
        String sql = "UPDATE review SET status = 1, version = version + 1 WHERE id = ? AND version = ?"; // 使用乐观锁，更新版本号
        try (Connection connection = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(connection, sql, review.getId(), review.getVersion()) > 0;
        }
    }

    @Override
    public boolean updateReviewResult(Integer reviewId, Boolean result) throws Exception {
        String sql = "UPDATE review SET result = ? WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(connection, sql, result, reviewId) > 0;
        }
    }

    @Override
    public List<Review> getPendingReviews() throws Exception {
        String sql = "SELECT * FROM review WHERE status = 0";
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql)) {
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        }
        return reviews;
    }

    @Override
    public Review getReviewById(Integer reviewId) throws Exception {
        String sql = "SELECT * FROM review WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, reviewId)) {
            if (rs.next()) {
                return mapResultSetToReview(rs);
            }
        }
        return null;
    }

    @Override
    public List<Review> getPendingReviewByPage(int begin, int pageSize) throws Exception {
        String sql = "SELECT * FROM review WHERE status = 0 LIMIT ?, ?";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, begin, pageSize)) {
            List<Review> reviews = new ArrayList<>();
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
            return reviews;
        }
    }

    @Override
    public int getTotalCount() throws Exception {
        String sql = "SELECT COUNT(*) FROM review WHERE status = 0";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Review mapResultSetToReview(ResultSet rs) throws Exception {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setUserId(rs.getInt("user_id"));
        review.setUploadWorkId(rs.getInt("uploadwork_id"));
        review.setContent(rs.getString("content"));
        review.setStatus(rs.getInt("status"));
        if(rs.getInt("status") == 0){

        }
        review.setUploadTime(rs.getTimestamp("upload_time").toLocalDateTime());
        review.setResult(rs.getBoolean("result"));
        review.setVersion(rs.getInt("version"));
        return review;
    }
}