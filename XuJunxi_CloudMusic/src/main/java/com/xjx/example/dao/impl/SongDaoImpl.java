package com.xjx.example.dao.impl;

import com.xjx.example.dao.SongDao;
import com.xjx.example.entity.Song;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongDaoImpl implements SongDao {

    @Override
    public Integer addSong(Song song) throws SQLException {
        String sql = "INSERT INTO song (title, author_id, album_id, genre, audio_url) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, song.getTitle());
            ps.setInt(2, song.getAuthorId());
            ps.setInt(3, song.getAlbumId());
            ps.setString(4, song.getGenre());
            ps.setString(5, song.getAudioUrl());
            ps.executeUpdate();

            // 获取自增主键
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateSong(Song song) throws SQLException {
        String sql = "UPDATE song SET title = ?, author_id = ?, album_id = ?, upload_time = ?, " +
                     "genre = ?, like_count = ?, play_count = ?, audio_url = ?, is_public = ?, is_vip_only = ? WHERE id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql,
                song.getTitle(), song.getAuthorId(), song.getAlbumId(),
                song.getUploadTime(), song.getGenre(), song.getLikeCount(), song.getPlayCount(), song.getAudioUrl(),song.isPublic(), song.isVipOnly(), song.getId()) > 0;
    }

    @Override
    public boolean deleteSong(int id) throws SQLException {
        String sql = "DELETE FROM song WHERE id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, id) > 0;
    }

    @Override
    public Song getSongById(int id) {
        String sql = "SELECT * FROM song WHERE id = ?";
        try (Connection connection = JDBCUtil.getConnection();
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, id)){
            if (rs.next()) {
                return mapResultSetToSong(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Song> getSongsByAuthorId(int authorId) {
        String sql = "SELECT * FROM song WHERE author_id = ?";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, authorId)){
            while (rs.next()) {
                songs.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    @Override
    public List<Song> getNonAlbumSongsByAuthorId(int authorId) {
        String sql = "SELECT * FROM song WHERE author_id = ? AND album_id = 0";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
            ResultSet rs = JDBCUtil.executeQuery(connection, sql, authorId)){
            while (rs.next()) {
                songs.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    @Override
    public List<Song> getAllSongsPublic() {
        String sql = "SELECT * FROM song WHERE is_public = true";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
            ResultSet rs = JDBCUtil.executeQuery(connection, sql)){
            while (rs.next()) {
                songs.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    @Override
    public List<Song> searchSongsByTitleWithSort(String keyword, int begin, int pageSize, String sortBy, String order) {
        List<Song> songs = new ArrayList<>();
        if (keyword == null || keyword.isEmpty()) {
            return songs;
        }

        // 构建SQL语句，使用动态排序字段和顺序
        String sql = "SELECT * FROM song WHERE title LIKE ? AND is_public = true ORDER BY " + sortBy + " " + order + " LIMIT ?, ?";

        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, "%" + keyword + "%", begin, pageSize)) {

            while (rs.next()) {
                songs.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    @Override
    public int getTotalCountByKeyword(String keyword) throws Exception {
        if (keyword == null || keyword.isEmpty()) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM song WHERE title LIKE ? AND is_public = true";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, "%" + keyword + "%")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Song> getSongsByAlbumId(int albumId) {
        String sql = "SELECT * FROM song WHERE album_id = ?";
        List<Song> songs = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, albumId)) {
            while (rs.next()) {
                songs.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return songs;
    }

    // 将ResultSet映射为Song对象
    private Song mapResultSetToSong(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setId(rs.getInt("id"));
        song.setTitle(rs.getString("title"));
        song.setAuthorId(rs.getInt("author_id"));
        song.setAlbumId(rs.getInt("album_id"));
        song.setUploadTime(rs.getTimestamp("upload_time").toLocalDateTime());
        song.setGenre(rs.getString("genre"));
        song.setLikeCount(rs.getInt("like_count"));
        song.setPlayCount(rs.getInt("play_count"));
        song.setAudioUrl(rs.getString("audio_url"));
        song.setPublic(rs.getBoolean("is_public"));
        song.setVipOnly(rs.getBoolean("is_vip_only"));
        return song;
    }
}