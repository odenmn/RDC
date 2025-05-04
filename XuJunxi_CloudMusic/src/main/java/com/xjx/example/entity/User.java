package com.xjx.example.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String username;
    private String email;
    private String password;
    private String salt; // 盐值
    private String avatar; // 头像
    private String phone; // 手机号
    private LocalDateTime createdAt;
    private Integer role = 0; // 0表示普通用户，1表示音乐人，2表示管理员
    private BigDecimal walletBalance = BigDecimal.ZERO;
    private LocalDateTime vipExpiry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public BigDecimal getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }

    public LocalDateTime getVipExpiry() {
        return vipExpiry;
    }

    public void setVipExpiry(LocalDateTime vipExpiry) {
        this.vipExpiry = vipExpiry;
    }
}