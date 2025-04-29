package com.xjx.example.dao;

import com.xjx.example.entity.Review;

import java.util.List;

public interface ReviewDao {
    // 插入审核记录
    boolean insertReview(Review review) throws Exception;

    // 更新审核状态
    boolean updateReviewStatus(Review review) throws Exception;

    // 新增方法：更新审核结果
    boolean updateReviewResult(Integer reviewId, Boolean result) throws Exception;

    // 获取所有待审核的记录
    List<Review> getPendingReviews() throws Exception;

    // 根据ID获取审核记录
    Review getReviewById(Integer reviewId) throws Exception;

    // 分页查询待审核的记录
    List<Review> getPendingReviewByPage(int begin, int pageSize) throws Exception;

    int getTotalCount() throws Exception;
}