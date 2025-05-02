package com.xjx.example.service;

import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Song;

import java.util.List;

public interface SongService {
    Integer addSong(Song song);
    boolean updateSong(Song song);
    boolean addSongsIntoAlbum(int[] songIds, int albumId);
    boolean removeSongFromAlbum(Song song);
    boolean approveSongPublic(Song song);
    boolean deleteSong(int id);
    Song getSongById(int id);
    List<Song> getSongsByAuthorId(int authorId);
    List<Song> getNonAlbumSongsByAuthorId(int authorId);

    PageBean<Song> searchSongsByTitleWithSort(String keyword, int currentPage, int pageSize, String sortBy, String order);

    List<Song> getRandomRecommendations(int count);

    List<Song> getSongsByAlbumId(int albumId);

    boolean increasePlayCount(int songId);
}