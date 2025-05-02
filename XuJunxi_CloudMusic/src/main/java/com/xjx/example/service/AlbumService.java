package com.xjx.example.service;

import com.xjx.example.entity.Album;
import com.xjx.example.entity.PageBean;

import java.util.List;

public interface AlbumService {
    // 添加专辑
    Integer addAlbum(Album album);

    // 删除专辑
    boolean deleteAlbum(int id);

    // 更新专辑
    boolean updateAlbum(Album album);

    boolean approveAlbumPublic(Album album);

    // 根据ID查询专辑
    Album getAlbumById(int id);

    // 根据作者ID查询专辑
    List<Album> getAlbumByAuthorId(int authorId);

    // 根据专辑标题精确查找专辑
    Album getAlbumByTitle(String title);

    // 根据专辑名模糊分页搜索专辑
    PageBean<Album> searchAlbumsByTitle(String keyword, int currentPage, int pageSize);

    // 获取随机专辑
    List<Album> getRandomRecommendations(int count);
}