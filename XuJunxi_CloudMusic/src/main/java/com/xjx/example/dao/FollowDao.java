package com.xjx.example.dao;

import java.sql.SQLException;

public interface FollowDao {
    boolean addFollow(int followerId, int followeeId) throws SQLException;
    boolean removeFollow(int followerId, int followeeId) throws SQLException;
    boolean isFollowing(int followerId, int followeeId) throws SQLException;
    int getFollowerCount(int userId) throws SQLException;
    int getFollowingCount(int userId) throws SQLException;
}