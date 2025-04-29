package com.xjx.example.dao.impl;

import com.xjx.example.dao.AlbumDao;
import com.xjx.example.entity.Album;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumDaoImpl implements AlbumDao {

    @Override
    public boolean addAlbum(Album album) throws SQLException {
        String sql = "INSERT INTO album (title, author, created_at, cover_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection()) {
            int rows = JDBCUtil.executeUpdate(conn, sql, album.getTitle(), album.getAuthorId(), album.getCreatedAt(), album.getCoverUrl());
            return rows > 0;
        }
    }

    @Override
    public boolean deleteAlbum(int id) throws SQLException {
        String sql = "DELETE FROM album WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            int rows = JDBCUtil.executeUpdate(conn, sql, id);
            return rows > 0;
        }
    }

    @Override
    public boolean updateAlbum(Album album) throws SQLException {
        String sql = "UPDATE album SET title = ?, author = ?, created_at = ?, cover_url = ? WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            int rows = JDBCUtil.executeUpdate(conn, sql, album.getTitle(), album.getAuthorId(), album.getCreatedAt(), album.getCoverUrl(), album.getId());
            return rows > 0;
        }
    }

    @Override
    public Album getAlbumById(int id) throws SQLException {
        String sql = "SELECT * FROM album WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, id)) {
                if (rs.next()) {
                    return mapToAlbum(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Album getAlbumByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM album WHERE title = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, title)) {
                if (rs.next()) {
                    return mapToAlbum(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Album> getAllAlbums() throws SQLException {
        String sql = "SELECT * FROM album";
        List<Album> albums = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql)) {
                while (rs.next()) {
                    albums.add(mapToAlbum(rs));
                }
            }
        }
        return albums;
    }

    @Override
    public List<Album> searchAlbumsByTitle(String keyword) throws SQLException {
        String sql = "SELECT * FROM album WHERE title LIKE ?";
        List<Album> albums = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, "%" + keyword + "%")) {
                while (rs.next()) {
                    albums.add(mapToAlbum(rs));
                }
            }
        }
        return albums;
    }

    @Override
    public List<Album> selectByPage(int begin, int pageSize) throws SQLException {
        String sql = "SELECT * FROM album LIMIT ?, ?";
        List<Album> albums = new ArrayList<>();
        try (Connection conn = JDBCUtil.getConnection()) {
            try (ResultSet rs = JDBCUtil.executeQuery(conn, sql, begin, pageSize)) {
                while (rs.next()) {
                    albums.add(mapToAlbum(rs));
                }
            }
        }
        return albums;
    }
    private Album mapToAlbum(ResultSet rs) throws SQLException {
        Album album = new Album();
        album.setId(rs.getInt("id"));
        album.setTitle(rs.getString("title"));
        album.setAuthorId(rs.getInt("author_id"));
        album.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        album.setCoverUrl(rs.getString("cover_url"));
        return album;
    }
}