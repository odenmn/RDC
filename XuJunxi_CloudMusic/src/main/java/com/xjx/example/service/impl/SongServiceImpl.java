package com.xjx.example.service.impl;

import com.xjx.example.dao.SongDao;
import com.xjx.example.dao.impl.SongDaoImpl;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Song;
import com.xjx.example.service.SongService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SongServiceImpl implements SongService {

    private static final Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongDao songDao = new SongDaoImpl();
    @Override
    public Integer addSong(Song song) {
        try {
            return songDao.addSong(song);
        } catch (SQLException e) {
            logger.error("添加歌曲失败: {}", song, e);
            throw new RuntimeException("添加歌曲失败", e);
        }
    }

    @Override
    public boolean updateSong(Song song) {
        try {
            return songDao.updateSong(song);
        } catch (SQLException e) {
            logger.warn("更新歌曲失败: {}", song, e);
            return false;
        }
    }

    @Override
    public boolean approveSongPublic(Song song) {
        try {
            song.setPublic(true);
            return songDao.updateSong(song);
        } catch (Exception e) {
            logger.warn("更新歌曲失败: {}", song, e);
            return false;
        }
    }

    @Override
    public boolean rejectSongPublic(Song song) {
        try {
            song.setPublic(false);
            return songDao.updateSong(song);
        } catch (Exception e) {
            logger.warn("更新歌曲失败: {}", song, e);
            return false;
        }
    }
    @Override
    public boolean deleteSong(int id) {
        try {
            return songDao.deleteSong(id);
        } catch (SQLException e) {
            logger.error("删除歌曲失败，ID: {}", id, e);
            throw new RuntimeException("删除歌曲失败，ID: " + id, e);
        }
    }

    @Override
    public Song getSongById(int id) {
        try {
            return songDao.getSongById(id);
        } catch (Exception e) {
            logger.warn("通过ID获取歌曲失败: {}", id, e);
            return null;
        }
    }

    @Override
    public List<Song> getAllSongs() {
        try {
            return songDao.getAllSongs();
        } catch (Exception e) {
            logger.error("获取所有歌曲失败", e);
            return null;
        }
    }

    @Override
    public PageBean<Song> searchSongsByTitle(String keyword, int currentPage, int pageSize) {
        try {
            int begin = (currentPage - 1) * pageSize;
            List<Song> songs = songDao.searchSongsByTitle(keyword, begin, pageSize);
            int totalCount = songDao.getTotalCountByKeyword(keyword);
            PageBean<Song> pageBean = new PageBean<>();
            pageBean.setTotalCount(totalCount);
            pageBean.setRows(songs);
            return pageBean;
        } catch (Exception e) {
            logger.warn("模糊搜索歌曲失败: {}", keyword, e);
            return null;
        }
    }

    @Override
    public List<Song> getRandomRecommendations(int count) {
        try {
            List<Song> allSongs = songDao.getAllSongs();
            Collections.shuffle(allSongs); // 打乱顺序
            return allSongs.subList(0, Math.min(count, allSongs.size()));
        } catch (Exception e) {
            logger.error("获取随机推荐失败", e);
            return null;
        }
    }


}