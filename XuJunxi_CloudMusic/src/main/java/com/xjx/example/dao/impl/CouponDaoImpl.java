package com.xjx.example.dao.impl;

import com.xjx.example.dao.CouponDao;
import com.xjx.example.entity.Coupon;
import com.xjx.example.util.JDBCUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CouponDaoImpl implements CouponDao {

    // 随机生成一个优惠券码
    private String generateCouponCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }
    @Override
    public int createCoupon(int userId) throws SQLException {
        String sql = "INSERT INTO coupon (user_id, coupon_code, discount_amount, expiration_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = JDBCUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            String couponCode = generateCouponCode();
            LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);

            pstmt.setInt(1, userId);
            pstmt.setString(2, couponCode);
            pstmt.setBigDecimal(3, BigDecimal.valueOf(10.00));
            pstmt.setObject(4, expirationDate);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    @Override
    // 判断用户是否已经领取过优惠券（防止重复领取）
    public boolean hasReceivedCoupon(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM coupon WHERE user_id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(conn, sql, userId)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public List<Coupon> getCouponsByUserId(int userId) throws SQLException {
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT * FROM coupon WHERE user_id = ? AND is_used = FALSE AND expiration_date > NOW()";
        try (Connection conn = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(conn, sql, userId)) {
            while (rs.next()) {
                Coupon coupon = mapResultSetToCoupon(rs);
                coupons.add(coupon);
            }
        }
        return coupons;
    }

    @Override
    public boolean useCoupon(int couponId) throws SQLException {
        String sql = "UPDATE coupon SET is_used = TRUE WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(conn, sql, couponId) > 0;
        }
    }

    private Coupon mapResultSetToCoupon(ResultSet rs) throws SQLException {
        Coupon coupon = new Coupon();
        coupon.setId(rs.getInt("id"));
        coupon.setUserId(rs.getInt("user_id"));
        coupon.setCouponCode(rs.getString("coupon_code"));
        coupon.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        coupon.setUsed(rs.getBoolean("is_used"));
        coupon.setExpirationDate(rs.getObject("expiration_date", LocalDateTime.class));
        return coupon;
    }

}
