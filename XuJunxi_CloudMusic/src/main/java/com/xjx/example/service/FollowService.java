package com.xjx.example.service;

import java.util.List;

public interface FollowService {
    boolean addFollow(int followerId, int followeeId);
    boolean removeFollow(int followerId, int followeeId);
    boolean isFollowing(int followerId, int followeeId);
    // 获取粉丝数
    int getFollowerCount(int userId);
    // 获取关注数
    int getFollowingCount(int userId);

    List<Integer> getFollowersByMusicianId(int musicianId);
}