package com.xjx.example.service;

import com.xjx.example.entity.Playlist;
import com.xjx.example.entity.Song;

import java.sql.SQLException;
import java.util.List;

public interface PlaylistService {
    boolean createPlaylist(Playlist playlist);
    boolean deletePlaylist(int playlistId);
    boolean addSongToPlaylist(int playlistId, int songId);
    boolean removeSongFromPlaylist(int playlistId, int songId);
    List<Playlist> getPlaylistsByUserId(int userId);
    List<Song> getSongsInPlaylist(int playlistId);

    boolean checkSongInPlaylist(int playlistId, int songId);
}