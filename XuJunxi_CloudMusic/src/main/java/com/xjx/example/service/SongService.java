package com.xjx.example.service;

import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Song;

import java.util.List;

public interface SongService {
    Integer addSong(Song song);
    boolean updateSong(Song song);

    boolean approveSongPublic(Song song);

    boolean rejectSongPublic(Song song);

    boolean deleteSong(int id);
    Song getSongById(int id);
    List<Song> getAllSongs();

    PageBean<Song> searchSongsByTitle(String keyword, int currentPage, int pageSize);

    List<Song> getRandomRecommendations(int count);
}