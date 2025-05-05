package com.xjx.example.dao;

import com.xjx.example.entity.Coupon;

import java.sql.SQLException;
import java.util.List;

public interface CouponDao {
    int createCoupon(int userId) throws SQLException;

    // 判断用户是否已经领取过优惠券（防止重复领取）
    boolean hasReceivedCoupon(int userId) throws SQLException;

    List<Coupon> getCouponsByUserId(int userId) throws SQLException;

    boolean useCoupon(int couponId) throws SQLException;
}
