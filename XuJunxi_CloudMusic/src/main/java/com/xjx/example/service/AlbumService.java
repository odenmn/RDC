package com.xjx.example.service;

import com.xjx.example.entity.Album;

import java.util.List;

public interface AlbumService {
    // 添加专辑
    boolean addAlbum(Album album);

    // 删除专辑
    boolean deleteAlbum(int id);

    // 更新专辑
    boolean updateAlbum(Album album);

    // 根据ID查询专辑
    Album getAlbumById(int id);

    // 查询所有专辑
    List<Album> getAllAlbums();

    // 新增方法：根据专辑标题精确查找专辑
    Album getAlbumByTitle(String title);

    // 新增方法：根据专辑名模糊搜索专辑
    List<Album> searchAlbumsByTitle(String keyword);
}