package com.xjx.example.dao.impl;

import com.xjx.example.dao.PlaylistDao;
import com.xjx.example.entity.Playlist;
import com.xjx.example.entity.Song;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDaoImpl implements PlaylistDao {

    @Override
    public boolean createPlaylist(Playlist playlist) throws SQLException {
        String sql = "INSERT INTO playlist (user_id, name, cover_url) VALUES (?, ?, ?)";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, playlist.getUserId(), playlist.getName(), playlist.getCoverUrl()) > 0;
    }

    @Override
    public boolean deletePlaylist(int playlistId) throws SQLException {
        String sql = "DELETE FROM playlist WHERE id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, playlistId) > 0;
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?, ?)";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, playlistId, songId) > 0;
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "DELETE FROM playlist_song WHERE playlist_id = ? AND song_id = ?";
        return JDBCUtil.executeUpdate(JDBCUtil.getConnection(), sql, playlistId, songId) > 0;
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM playlist WHERE user_id = ?";
        List<Playlist> playlists = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, userId)) {
            while (rs.next()) {
                playlists.add(mapResultSetToPlaylist(rs));
            }
        }
        return playlists;
    }

    @Override
    public List<Integer> getSongsInPlaylist(int playlistId) throws SQLException {
        String sql = "SELECT song_id FROM playlist_song WHERE playlist_id = ?";
        List<Integer> songIds = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, playlistId)) {
            while (rs.next()) {
                songIds.add(rs.getInt("song_id"));
            }
        }
        return songIds;
    }

    @Override
    public boolean checkSongInPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "SELECT * FROM playlist_song WHERE playlist_id = ? AND song_id = ?";
        try (Connection connection = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(connection, sql, playlistId, songId)) {
            return rs.next();
        }
    }

    public Playlist mapResultSetToPlaylist(ResultSet rs) throws SQLException {
        Playlist playlist = new Playlist();
        playlist.setId(rs.getInt("id"));
        playlist.setName(rs.getString("name"));
        playlist.setUserId(rs.getInt("user_id"));
        playlist.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        playlist.setCoverUrl(rs.getString("cover_url"));
        return playlist;
    }
}