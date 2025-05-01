package com.xjx.example.service.impl;

import com.xjx.example.dao.SongDao;
import com.xjx.example.dao.impl.SongDaoImpl;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.Song;
import com.xjx.example.service.SongService;
import com.xjx.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class SongServiceImpl implements SongService {

    private static final Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);
    private final SongDao songDao = new SongDaoImpl();
    private final UserService userService = new UserServiceImpl();
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
    public boolean addSongsIntoAlbum(int[] songIds, int albumId) {
        try {
            for (int songId : songIds) {
                Song song = songDao.getSongById(songId);
                song.setAlbumId(albumId);
                if (!songDao.updateSong(song)) {
                  return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.warn("更新歌曲失败: ",  e);
            return false;
        }
    }

    @Override
    public boolean removeSongFromAlbum(Song song) {
        try {
            song.setAlbumId(0);
            return songDao.updateSong(song);
        } catch (Exception e) {
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
    public List<Song> getSongsByAuthorId(int authorId) {
        try {
            return songDao.getSongsByAuthorId(authorId);
        } catch (Exception e) {
            logger.warn("通过作者ID获取歌曲失败: {}", authorId, e);
            return null;
        }
    }

    @Override
    public List<Song> getNonAlbumSongsByAuthorId(int authorId) {
        try {
            return songDao.getNonAlbumSongsByAuthorId(authorId);
        } catch (Exception e) {
            logger.warn("通过作者ID获取非专辑歌曲失败: {}", authorId, e);
            return null;
        }
    }
    @Override
    public PageBean<Song> searchSongsByTitle(String keyword, int currentPage, int pageSize) {
        try {
            int begin = (currentPage - 1) * pageSize;
            List<Song> songs = songDao.searchSongsByTitle(keyword, begin, pageSize);
            // 获取作者名
            for (Song song: songs) {
                String authorName = userService.getUserById(song.getAuthorId()).getUsername();
                song.setAuthorName(authorName);
            }
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
            List<Song> allSongs = songDao.getAllSongsPublic();
            Collections.shuffle(allSongs); // 打乱顺序
            return allSongs.subList(0, Math.min(count, allSongs.size()));
        } catch (Exception e) {
            logger.error("获取随机推荐失败", e);
            return null;
        }
    }

    @Override
    public List<Song> getSongsByAlbumId(int albumId) {
        try {
            return songDao.getSongsByAlbumId(albumId);
        } catch (Exception e) {
            logger.warn("通过专辑ID获取歌曲失败: {}", albumId, e);
            return null;
        }
    }

    @Override
    public boolean increasePlayCount(int songId) {
        try {
            Song song = songDao.getSongById(songId);
            song.setPlayCount(song.getPlayCount() + 1);
            return songDao.updateSong(song);
        } catch (Exception e) {
            logger.warn("更新歌曲播放次数失败: {}", songId, e);
            return false;
        }
    }

}