package com.xjx.example.service.impl;

import com.xjx.example.dao.FollowDao;
import com.xjx.example.dao.impl.FollowDaoImpl;
import com.xjx.example.service.FollowService;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FollowServiceImpl implements FollowService {

    private static final Logger logger = Logger.getLogger(FollowServiceImpl.class.getName());
    private final FollowDao followDao = new FollowDaoImpl();

    @Override
    public boolean addFollow(int followerId, int followeeId) {
        try {
            return followDao.addFollow(followerId, followeeId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "添加关注失败，关注者ID: " + followerId + "，被关注者ID: " + followeeId, e);
            return false;
        }
    }

    @Override
    public boolean removeFollow(int followerId, int followeeId) {
        try {
            return followDao.removeFollow(followerId, followeeId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "取消关注失败，关注者ID: " + followerId + "，被关注者ID: " + followeeId, e);
            return false;
        }
    }

    @Override
    public boolean isFollowing(int followerId, int followeeId) {
        try {
            return followDao.isFollowing(followerId, followeeId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "检查关注状态失败，关注者ID: " + followerId + "，被关注者ID: " + followeeId, e);
            return false;
        }
    }

    @Override
    public int getFollowerCount(int userId) {
        try {
            return followDao.getFollowerCount(userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取粉丝数失败，用户ID: " + userId, e);
            return 0;
        }
    }

    @Override
    public int getFollowingCount(int userId) {
        try {
            return followDao.getFollowingCount(userId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取关注数失败，用户ID: " + userId, e);
            return 0;
        }
    }

    @Override
    public List<Integer> getFollowersByMusicianId(int musicianId) {
        try {
            return followDao.getFollowersByMusicianId(musicianId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取粉丝列表失败，音乐人ID: " + musicianId, e);
            return null;
        }
    }
}