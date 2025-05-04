package com.xjx.example.dao;

import com.xjx.example.entity.VipPlan;

import java.sql.SQLException;
import java.util.List;

public interface VipPlanDao {
    List<VipPlan> getAllPlans() throws SQLException;

    VipPlan getPlanById(int id) throws SQLException;
}
