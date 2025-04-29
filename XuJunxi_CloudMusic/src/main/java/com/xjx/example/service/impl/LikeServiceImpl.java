package com.xjx.example.service.impl;

import com.xjx.example.dao.LikeDao;
import com.xjx.example.dao.impl.LikeDaoImpl;
import com.xjx.example.entity.Song;
import com.xjx.example.service.LikeService;
import com.xjx.example.service.SongService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LikeServiceImpl implements LikeService {

    private static final Logger logger = Logger.getLogger(LikeServiceImpl.class.getName());
    private final LikeDao likeDao = new LikeDaoImpl();

    @Override
    public boolean addLike(int userId, int songId) {
        try {
            return likeDao.addLike(userId, songId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "添加点赞失败，用户ID: " + userId + "，歌曲ID: " + songId, e);
            return false;
        }
    }

    @Override
    public boolean removeLike(int userId, int songId) {
        try {
            return likeDao.removeLike(userId, songId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "取消点赞失败，用户ID: " + userId + "，歌曲ID: " + songId, e);
            return false;
        }
    }

    @Override
    public boolean isLiked(int userId, int songId) {
        try {
            return likeDao.isLiked(userId, songId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "检查点赞状态失败，用户ID: " + userId + "，歌曲ID: " + songId, e);
            return false;
        }
    }

    @Override
    public int getLikeCount(int songId) {
        try {
            return likeDao.getLikeCount(songId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "获取点赞数失败，歌曲ID: " + songId, e);
            return 0;
        }
    }
}