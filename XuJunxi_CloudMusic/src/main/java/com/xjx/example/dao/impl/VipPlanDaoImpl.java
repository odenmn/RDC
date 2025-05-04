package com.xjx.example.dao.impl;

import com.xjx.example.dao.VipPlanDao;
import com.xjx.example.entity.VipPlan;
import com.xjx.example.util.JDBCUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VipPlanDaoImpl implements VipPlanDao {
    @Override
    public List<VipPlan> getAllPlans() throws SQLException {
        List<VipPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM vip_plan";
        try (Connection conn = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(conn, sql)) {
            while (rs.next()) {
                plans.add(mapResultSetToVipPlan(rs));
            }
        }
        return plans;
    }

    @Override
    public VipPlan getPlanById(int id) throws SQLException {
        String sql = "SELECT * FROM vip_plan WHERE id = ?";
        try (Connection conn = JDBCUtil.getConnection();
             ResultSet rs = JDBCUtil.executeQuery(conn, sql, id)) {
            if (rs.next()) {
                return mapResultSetToVipPlan(rs);
            }
        }
        return null;
    }
    private VipPlan mapResultSetToVipPlan(ResultSet rs) throws SQLException {
        VipPlan vipPlan = new VipPlan();
        vipPlan.setId(rs.getInt("id"));
        vipPlan.setName(rs.getString("name"));
        vipPlan.setDurationDays(rs.getInt("duration_days"));
        vipPlan.setPrice(rs.getBigDecimal("price"));
        return vipPlan;
    }
}
