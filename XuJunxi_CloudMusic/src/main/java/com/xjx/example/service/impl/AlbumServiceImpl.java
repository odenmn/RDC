package com.xjx.example.service.impl;

import com.xjx.example.dao.AlbumDao;
import com.xjx.example.dao.impl.AlbumDaoImpl;
import com.xjx.example.entity.Album;
import com.xjx.example.service.AlbumService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumServiceImpl implements AlbumService {

    private final AlbumDao albumDao = new AlbumDaoImpl();

    @Override
    public boolean addAlbum(Album album) {
        try {
            return albumDao.addAlbum(album);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
    public Album getAlbumById(int id) {
        try {
            return albumDao.getAlbumById(id);
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
    public List<Album> getAllAlbums() {
        try {
            return albumDao.getAllAlbums();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Album> searchAlbumsByTitle(String keyword) {
        try {
            return albumDao.searchAlbumsByTitle(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}