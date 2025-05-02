package com.xjx.example.dao;

import com.xjx.example.entity.Album;

import java.sql.SQLException;
import java.util.List;

public interface AlbumDao {
    // 添加专辑
    Integer addAlbum(Album album) throws SQLException;

    // 删除专辑
    boolean deleteAlbum(int id) throws SQLException;

    // 更新专辑
    boolean updateAlbum(Album album) throws SQLException;

    // 根据ID查询专辑
    Album getAlbumById(int id) throws SQLException;

    // 查询所有专辑
    List<Album> getAllAlbumsPublic() throws SQLException;

    // 根据专辑标题精确查找专辑
    Album getAlbumByTitle(String title) throws SQLException;

    // 根据专辑名模糊分页搜索专辑
    List<Album> searchAlbumsByTitle(String keyword, int begin, int pageSize);
    int getTotalCountByKeyword(String keyword);


    List<Album> getAlbumByAuthorId(int authorId) throws SQLException;
}