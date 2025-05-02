package com.xjx.example.service.impl;

import com.xjx.example.dao.PlaylistDao;
import com.xjx.example.dao.impl.PlaylistDaoImpl;
import com.xjx.example.entity.Playlist;
import com.xjx.example.entity.Song;
import com.xjx.example.service.PlaylistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistServiceImpl.class);
    private final PlaylistDao playlistDao = new PlaylistDaoImpl();
    private final SongServiceImpl songService = new SongServiceImpl();
    @Override
    public boolean createPlaylist(Playlist playlist) {
        try {
            return playlistDao.createPlaylist(playlist);
        } catch (SQLException e) {
            logger.error("Error creating playlist for user {}: {}, Exception: {}",playlist.getUserId(), playlist, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deletePlaylist(int playlistId) {
        try {
            return playlistDao.deletePlaylist(playlistId);
        } catch (SQLException e) {
            logger.error("Error deleting playlist: {}, Exception: {}", playlistId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId) {
        try {
            return playlistDao.addSongToPlaylist(playlistId, songId);
        } catch (SQLException e) {
            logger.error("Error adding song to playlist {}: {}, Exception: {}", playlistId, songId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        try {
            return playlistDao.removeSongFromPlaylist(playlistId, songId);
        } catch (SQLException e) {
            logger.error("Error removing song from playlist {}: {}, Exception: {}", playlistId, songId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Playlist> getPlaylistsByUserId(int userId) {
        try {
            return playlistDao.getPlaylistsByUserId(userId);
        } catch (SQLException e) {
            logger.error("Error retrieving playlists for user: {}, Exception: {}", userId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId) {
        try {
            List<Integer> songIds = playlistDao.getSongsInPlaylist(playlistId);
            List<Song> songs = new ArrayList<>();
            for (Integer songId : songIds) {
                Song song = songService.getSongById(songId);
                if (song != null) {
                    songs.add(song);
                }
            }
            return songs;
        } catch (SQLException e) {
            logger.error("Error retrieving songs in playlist: {}, Exception: {}", playlistId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean checkSongInPlaylist(int playlistId, int songId) {
        try {
            return playlistDao.checkSongInPlaylist(playlistId, songId);
        } catch (SQLException e) {
            logger.error("Error checking song in playlist: {}, Exception: {}", playlistId, e.getMessage(), e);
            return false;
        }
    }
}