package com.xjx.example.dao;

import com.xjx.example.entity.Playlist;

import java.sql.SQLException;
import java.util.List;

public interface PlaylistDao {
    boolean createPlaylist(Playlist playlist) throws SQLException;
    boolean deletePlaylist(int playlistId) throws SQLException;
    boolean addSongToPlaylist(int playlistId, int songId) throws SQLException;
    boolean removeSongFromPlaylist(int playlistId, int songId) throws SQLException;
    List<Playlist> getPlaylistsByUserId(int userId) throws SQLException;
    List<Integer> getSongsInPlaylist(int playlistId) throws SQLException;
    boolean checkSongInPlaylist(int playlistId, int songId) throws SQLException;
}