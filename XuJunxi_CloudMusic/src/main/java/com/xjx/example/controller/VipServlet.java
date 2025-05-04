package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.User;
import com.xjx.example.entity.VipPlan;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.UserServiceImpl;
import com.xjx.example.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/vip/*")
public class VipServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();

    public void getVipStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        User user = (User) request.getSession().getAttribute("user");
        boolean isVip = userService.isUserVip(user);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isVip", isVip);
        response.getWriter().write(jsonObject.toJSONString());
    }
    public void getAllVipPlans(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取所有VIP计划
        List<VipPlan> vipPlans = userService.getAllVipPlans();
        String json = JSON.toJSONString(vipPlans);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    public void rechargeWallet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = JsonUtil.getJsonObject(request);
        BigDecimal amount = json.getBigDecimal("amount");
        // 判断充值金额是否小于等于0
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", false);
            jsonObject.put("message", "充值金额必须大于0");
            response.getWriter().write(jsonObject.toJSONString());
            return;
        }
        User user = (User) request.getSession().getAttribute("user");
        boolean success = userService.rechargeWallet(user, json.getBigDecimal("amount"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        if (success) {
            jsonObject.put("message", "充值成功");
        } else {
            jsonObject.put("message", "充值失败");
        }
        response.getWriter().write(jsonObject.toJSONString());
    }
    public void purchaseVip(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        // 获取用户和VIP计划ID
        User user = (User) request.getSession().getAttribute("user");
        JSONObject json = JsonUtil.getJsonObject(request);
        int planId = json.getInteger("planId");
        // VIP购买
        boolean success = userService.purchaseVip(user, planId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", success);
        if (success) {
            jsonObject.put("message", "购买成功");
        } else {
            jsonObject.put("message", "购买失败");
        }
        response.getWriter().write(jsonObject.toJSONString());
    }


}
