package com.xjx.example.service.impl;

import com.xjx.example.dao.AssistRecordDao;
import com.xjx.example.dao.CouponDao;
import com.xjx.example.dao.impl.AssistRecordDaoImpl;
import com.xjx.example.dao.impl.CouponDaoImpl;
import com.xjx.example.entity.Coupon;
import com.xjx.example.entity.Message;
import com.xjx.example.service.CouponService;
import com.xjx.example.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class CouponServiceImpl implements CouponService {
    private final CouponDao couponDao = new CouponDaoImpl();
    private final AssistRecordDao assistRecordDao = new AssistRecordDaoImpl();
    private final MessageService messageService = new MessageServiceImpl();
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Override
    public boolean hasUserAssisted(int invitedId) {
        try {
            // 检查该被邀请的用户是否已经助力过
            return assistRecordDao.hasUserAssisted(invitedId);
        } catch (SQLException e) {
            logger.error("检查用户是否已经助力过程中发生异常：", e);
            return false;
        }
    }

    @Override
    public boolean assistByLinkAndCreateCoupon(int inviterUserId, int invitedUserId, String assistToken) {
        try {
            int couponId = couponDao.createCoupon(inviterUserId);
            if (couponId > 0) {
                if (assistRecordDao.addAssistRecord(couponId, invitedUserId, assistToken)) {
                    // 创建成功，发送消息给被邀请的用户
                    Message message = new Message();
                    message.setSenderId(invitedUserId);
                    message.setReceiverId(inviterUserId);
                    message.setTitle("优惠券通知");
                    message.setContent("您已获得一张优惠券，请前往我的优惠券查看。");
                    boolean result1 = messageService.sendMessage(message);

                    // 创建助力记录
                    boolean result2 = assistRecordDao.addAssistRecord(couponId, inviterUserId, assistToken);

                    return result1 && result2;
                }
            }
        } catch (SQLException e) {
            logger.error("通过链接助力并创建优惠券过程中发生异常：", e);
            return false;
        }
        return false;
    }


    // 检查用户是否可以领取优惠券（是否已有未使用的）
    @Override
    public boolean canReceiveCoupon(int userId) {
        try {
            return !couponDao.hasReceivedCoupon(userId);
        } catch (SQLException e) {
            logger.error("检查用户是否可以领取优惠券过程中发生异常：", e);
            return false;
        }
    }

    // 获取用户所有未使用且有效的优惠券
    @Override
    public List<Coupon> getAvailableCoupons(int userId) {
        try {
            return couponDao.getCouponsByUserId(userId);
        } catch (SQLException e) {
            logger.error("获取用户所有未使用且有效的优惠券过程中发生异常：", e);
            return null;
        }
    }

    @Override
    public boolean useCoupon(int couponId) {
        try {
            return couponDao.useCoupon(couponId);
        } catch (SQLException e) {
            logger.error("使用优惠券过程中发生异常：", e);
            return false;
        }
    }

}
