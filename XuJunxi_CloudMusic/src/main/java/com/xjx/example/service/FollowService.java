package com.xjx.example.service;

public interface FollowService {
    boolean addFollow(int followerId, int followeeId);
    boolean removeFollow(int followerId, int followeeId);
    boolean isFollowing(int followerId, int followeeId);
    int getFollowerCount(int userId);
    int getFollowingCount(int userId);
}