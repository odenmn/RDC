package com.xjx.example.service;

import com.xjx.example.entity.Coupon;

import java.util.List;

public interface CouponService {

    // 判断用户是否已经助力过
    boolean hasUserAssisted(int invitedId);

    // 用户B助力成功后，用户A获得一张优惠券
    boolean assistByLinkAndCreateCoupon(int inviterUserId, int invitedUserId, String assistToken);

    // 检查用户是否可以领取优惠券（是否已有未使用的）
    boolean canReceiveCoupon(int userId);

    // 获取用户所有未使用且有效的优惠券
    List<Coupon> getAvailableCoupons(int userId);

    boolean useCoupon(int couponId);
}
