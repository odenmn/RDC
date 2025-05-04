package com.xjx.example.service;

import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.User;
import com.xjx.example.entity.VipPlan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    boolean addUser(User user);
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    User getUserByEmail(String email);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    boolean register(User user);
    boolean adminRegister(User user, String adminInviteCode);
    User login(String username, String password);
    boolean resetPassword(String email, String newPassword);

    boolean updatePhone(User user, String newPhone);

    boolean updateAvatar(User user, String newAvatar);

    boolean becomeMusician(User user);

    PageBean<User> searchUsersByUsername(String keyword, int currentPage, int pageSize);

    boolean rechargeWallet(User user, BigDecimal amount);

    boolean purchaseVip(User user, int planId);

    boolean isUserVip(User user);

    List<VipPlan> getAllVipPlans();
}