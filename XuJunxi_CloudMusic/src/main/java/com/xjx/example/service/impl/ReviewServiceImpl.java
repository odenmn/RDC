package com.xjx.example.service.impl;

import com.xjx.example.dao.ReviewDao;
import com.xjx.example.dao.impl.ReviewDaoImpl;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Review;
import com.xjx.example.service.ReviewService;
import com.xjx.example.service.SongService;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewDao reviewDao = new ReviewDaoImpl();
    private final SongService songService = new SongServiceImpl();
    @Override
    public boolean submitReview(Review review) {
        try {
            return reviewDao.insertReview(review);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean approveReview(Integer reviewId) {
        try {
            return reviewDao.updateReviewResult(reviewId, true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean rejectReview(Integer reviewId) {
        try {
            return reviewDao.updateReviewResult(reviewId, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean processingReview(Review review) {
        try {
            return reviewDao.updateReviewStatus(review);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Review> getPendingReviews() {
        try {
            return reviewDao.getPendingReviews();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        try {
            return reviewDao.getReviewById(reviewId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PageBean<Review> getPendingReviewByPage(int currentPage, int pageSize) {
        try {
            int begin = (currentPage - 1) * pageSize;
            List<Review> reviews = reviewDao.getPendingReviewByPage(begin, pageSize);
            for (Review review : reviews) {
                if (review.getStatus() == 0){
                    review.setStatusStr("待审核");
                } else {
                    review.setStatusStr("已审核");
                }
            }
            int totalCount = reviewDao.getTotalCount();
            PageBean<Review> pageBean = new PageBean<>();
            pageBean.setRows(reviews);
            pageBean.setTotalCount(totalCount);
            return pageBean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}