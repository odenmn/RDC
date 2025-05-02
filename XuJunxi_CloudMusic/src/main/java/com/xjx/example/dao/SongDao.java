package com.xjx.example.dao;

import com.xjx.example.entity.Song;

import java.sql.SQLException;
import java.util.List;

public interface SongDao {
    Integer addSong(Song song) throws SQLException;
    boolean updateSong(Song song) throws SQLException;
    boolean deleteSong(int id) throws SQLException;
    Song getSongById(int id);
    List<Song> getSongsByAuthorId(int authorId);
    // 获取非专辑歌曲
    List<Song> getNonAlbumSongsByAuthorId(int authorId);
    List<Song> getAllSongsPublic();
    // 模糊搜索方法
    List<Song> searchSongsByTitleWithSort(String keyword, int begin, int pageSize, String sortBy, String order);
    int getTotalCountByKeyword(String keyword) throws Exception;
    List<Song> getSongsByAlbumId(int albumId);
}