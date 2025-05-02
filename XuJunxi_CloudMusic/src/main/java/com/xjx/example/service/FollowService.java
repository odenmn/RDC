package com.xjx.example.service;

import java.util.List;

public interface FollowService {
    boolean addFollow(int followerId, int followeeId);
    boolean removeFollow(int followerId, int followeeId);
    boolean isFollowing(int followerId, int followeeId);
    int getFollowerCount(int userId);
    int getFollowingCount(int userId);

    List<Integer> getFollowersByMusicianId(int musicianId);
}