package com.xjx.example.service;

import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Review;

import java.util.List;

public interface ReviewService {
    // 提交审核
    boolean submitReview(Review review);

    // 审核通过
    boolean approveReview(Integer reviewId);

    // 审核不通过
    boolean rejectReview(Integer reviewId);

    // 设置审核状态为处理中
    boolean processingReview(Review review);

    // 获取所有待审核的记录
    List<Review> getPendingReviews();

    // 根据ID获取审核记录
    Review getReviewById(Integer reviewId);

    PageBean<Review> getPendingReviewByPage(int currentPage, int pageSize);
}