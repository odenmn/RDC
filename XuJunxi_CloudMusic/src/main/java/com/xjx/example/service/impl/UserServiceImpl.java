package com.xjx.example.service.impl;

import com.xjx.example.dao.UserDao;
import com.xjx.example.dao.VipPlanDao;
import com.xjx.example.dao.impl.UserDaoImpl;
import com.xjx.example.dao.impl.VipPlanDaoImpl;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.User;
import com.xjx.example.entity.VipPlan;
import com.xjx.example.service.UserService;
import com.xjx.example.util.PasswordEncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDao userDao = new UserDaoImpl();
    private final VipPlanDao vipPlanDao = new VipPlanDaoImpl();
    @Override
    public boolean addUser(User user) {
        try {
            return userDao.addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserById(int id) {
        try {
            return userDao.getUserById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取用户失败，请稍后重试");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return userDao.getUserByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取用户失败，请稍后重试");
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDao.getAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取用户列表失败，请稍后重试");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return userDao.getUserByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("通过邮箱获取用户失败，请稍后重试");
        }
    }

    @Override
    public boolean updateUser(User user) {
        try {
            return userDao.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(int id) {
        try {
            return userDao.deleteUser(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean register(User user) {
        // 先检查用户名和邮箱是否已存在
        User nameExistingUser = getUserByUsername(user.getUsername());
        User emailExistingUser = getUserByEmail(user.getEmail());
        if (nameExistingUser != null && emailExistingUser != null) {
            return false;
        }
        // 生成盐值
        String salt = UUID.randomUUID().toString();
        // 对密码进行加密
        user.setSalt(salt);
        user.setPassword(PasswordEncryptUtil.encryptPassword(user.getPassword() + salt));
        System.out.println("用户注册成功: " + user.getUsername());
        return addUser(user);
    }
    @Override
    public boolean adminRegister(User user, String adminInviteCode) {
        System.out.println("邀请码：" + adminInviteCode);
        // 校验管理员邀请码
        if ("ADMIN_INVITE_CODE".equals(adminInviteCode)) { // 假设管理员邀请码为 "ADMIN_INVITE_CODE"
            user.setRole(2); // 设置用户为管理员，角色值为 2
        } else {
            return false;
        }
        return register(user);
    }
    @Override
    public User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            System.out.println("用户登录失败: 用户名不存在 - " + username);
            return null;
        }
        String encryptedPassword = PasswordEncryptUtil.encryptPassword(password + user.getSalt());
        if (encryptedPassword.equals(user.getPassword())) {
            System.out.println("用户登录成功: " + username);
            return user;
        }else{
            System.out.println("用户登录失败: 密码错误 - " + username);
        }
        return null;
    }

    @Override
    public boolean resetPassword(String email, String newPassword) {
        User user = getUserByEmail(email);
        if (user == null) {
            System.out.println("重置密码失败: 邮箱不存在 - " + email);
            return false;
        }
        if (user.getEmail().equals(email)) {
            user.setPassword(PasswordEncryptUtil.encryptPassword(newPassword + user.getSalt()));
            updateUser(user);
            System.out.println("用户密码重置成功: " + user.getUsername() + "-->" + newPassword);
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePhone(User user, String newPhone) {
        try {
            user.setPhone(newPhone);
            return userDao.updateUser(user);
        } catch (SQLException e) {
            logger.error("更新手机号失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateAvatar(User user, String newAvatar) {
        try {
            user.setAvatar(newAvatar);
            return userDao.updateUser(user);
        } catch (SQLException e) {
            logger.error("更新头像失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean becomeMusician(User user) {
        try {
            // 更新用户状态为音乐人
            user.setRole(1);
            return userDao.updateUser(user);
        } catch (SQLException e) {
            logger.error("认证为音乐人失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public PageBean<User> searchUsersByUsername(String keyword, int currentPage, int pageSize) {
        try {
            int begin = (currentPage - 1) * pageSize;
            List<User> users = userDao.searchUsersByUsername(keyword, begin, pageSize);
            int totalCount = userDao.getTotalCountByKeyword(keyword);

            PageBean<User> pageBean = new PageBean<>();
            pageBean.setTotalCount(totalCount);
            pageBean.setRows(users);
            return pageBean;
        } catch (Exception e) {
            logger.warn("模糊搜索用户失败: {}", keyword, e);
            return null;
        }
    }

    @Override
    public boolean rechargeWallet(User user, BigDecimal amount) {
        try {
            user.setWalletBalance(user.getWalletBalance().add(amount));
            return userDao.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean purchaseVip(User user, int planId) {
        try {
            VipPlan plan = vipPlanDao.getPlanById(planId);

            if (user.getWalletBalance().compareTo(plan.getPrice()) < 0) {
                return false;
            }

            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();

            // 计算新的到期时间
            LocalDateTime newExpiry;
            if (user.getVipExpiry() != null && user.getVipExpiry().isAfter(now)) {
                // 如果已有VIP，从到期时间继续叠加
                newExpiry = user.getVipExpiry().plusDays(plan.getDurationDays());
            } else {
                // 否则从当前时间开始
                newExpiry = now.plusDays(plan.getDurationDays());
            }

            // 扣款 + 更新VIP时间
            user.setWalletBalance(user.getWalletBalance().subtract(plan.getPrice()));
            user.setVipExpiry(newExpiry);
            return userDao.updateUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isUserVip(User user) {
        if (user.getVipExpiry() == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(user.getVipExpiry());
    }

    @Override
    public List<VipPlan> getAllVipPlans() {
        try {
            return vipPlanDao.getAllPlans();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
