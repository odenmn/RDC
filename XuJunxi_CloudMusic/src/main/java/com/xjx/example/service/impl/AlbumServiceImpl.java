package com.xjx.example.service.impl;

import com.xjx.example.dao.AlbumDao;
import com.xjx.example.dao.impl.AlbumDaoImpl;
import com.xjx.example.entity.Album;
import com.xjx.example.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class AlbumServiceImpl implements AlbumService {
    private static final Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);

    private final AlbumDao albumDao = new AlbumDaoImpl();

    @Override
    public Integer addAlbum(Album album) {
        try {
            return albumDao.addAlbum(album);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteAlbum(int id) {
        try {
            return albumDao.deleteAlbum(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateAlbum(Album album) {
        try {
            return albumDao.updateAlbum(album);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean approveAlbumPublic(Album album) {
        try {
            album.setPublic(true);
            return albumDao.updateAlbum(album);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Album getAlbumById(int id) {
        try {
            return albumDao.getAlbumById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Album> getAlbumByAuthorId(int authorId) {
        try {
            return albumDao.getAlbumByAuthorId(authorId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Album getAlbumByTitle(String title) {
        try {
            return albumDao.getAlbumByTitle(title);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Album> searchAlbumsByTitle(String keyword) {
        try {
            return albumDao.searchAlbumsByTitle(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<Album> getRandomRecommendations(int count) {
        try {
            List<Album> allAlbumIds = albumDao.getAllAlbumsPublic();
            Collections.shuffle(allAlbumIds); // 打乱顺序
            return allAlbumIds.subList(0, Math.min(count, allAlbumIds.size()));
        } catch (Exception e) {
            logger.error("获取随机推荐失败", e);
            return null;
        }
    }
}