package com.xjx.example.dao;

import com.xjx.example.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    boolean addUser(User user) throws SQLException;
    User getUserById(int id) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    User getUserByEmail(String email) throws SQLException;

    List<User> searchUsersByUsername(String keyword, int begin, int pageSize);

    int getTotalCountByKeyword(String keyword);
}