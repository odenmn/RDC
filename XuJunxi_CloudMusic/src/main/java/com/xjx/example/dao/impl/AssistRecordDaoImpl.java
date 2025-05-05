package com.xjx.example.dao.impl;

import com.xjx.example.dao.AssistRecordDao;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssistRecordDaoImpl implements AssistRecordDao {
    @Override
    public boolean addAssistRecord(int couponId, int friendId, String assistValue) throws SQLException {
        String sql = "INSERT INTO assist_record (coupon_id, friend_id, assist_value) VALUES (?, ?, ?)";
        try (Connection conn = JDBCUtil.getConnection()) {
            return JDBCUtil.executeUpdate(conn, sql, couponId, friendId, assistValue) > 0;
        }
    }

    @Override
    public boolean hasUserAssisted(int invitedId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM assist_record WHERE friend_id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(conn, sql, invitedId)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
