package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.Coupon;
import com.xjx.example.entity.Message;
import com.xjx.example.entity.User;
import com.xjx.example.service.CouponService;
import com.xjx.example.service.MessageService;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.CouponServiceImpl;
import com.xjx.example.service.impl.MessageServiceImpl;
import com.xjx.example.service.impl.UserServiceImpl;
import com.xjx.example.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/coupon/*")
public class CouponServlet extends BaseServlet {
    private final CouponService couponService = new CouponServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final MessageService messageService = new MessageServiceImpl();

    public void inviteFriend(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        JSONObject jsonResponse = JsonUtil.getJsonObject(request);
        // 获取当前助力人ID
        String invitedIdStr = jsonResponse.getString("invitedId");
        if (invitedIdStr == null || invitedIdStr.isEmpty()) {
            response.getWriter().write("缺少被邀请人ID");
            return;
        }

        int invitedId = Integer.parseInt(invitedIdStr);

        // 判断被邀请人是否存在
        if (userService.getUserById(invitedId) == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "被邀请人ID不存在");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        User invitedUser = userService.getUserById(invitedId);

        // 判断被邀请人是否为管理员
        if (invitedUser.getRole() == 2) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "此用户无法被邀请助力！");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        // 判断被邀请人是否为新人
        if (invitedUser.getCreatedAt().plusDays(7).isAfter(LocalDateTime.now())) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "该用户不是新人，无法参与助力");
        }

        if (couponService.hasUserAssisted(invitedId)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "该用户已经助力过");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        // 生成助力链接
        String assistToken = "ASSIST_TOKEN_" + System.currentTimeMillis();
        String assistLink = "/XuJunxi_CloudMusic/coupon/completeAssist?inviterId=" + user.getId() + "&friendId=" + invitedId + "&token=" + assistToken;

        // 构造带链接的消息
        Message message = new Message();
        message.setSenderId(user.getId());
        message.setReceiverId(invitedId);
        message.setTitle("好友助力通知");
        message.setContent(String.format(
                "用户 %s 邀请您助力！\n" +
                        "请点击下方链接完成助力，您将帮助他获得一张10元无门槛VIP优惠券。\n" +
                        "🔗 助力链接: <a href='%s'>%s</a>",
                user.getUsername(), assistLink, assistLink
        ));

        // 发送消息
        if (messageService.sendMessage(message)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "已发送邀请助力消息");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "发送邀请助力消息失败");
        }
        response.getWriter().write(jsonResponse.toJSONString());

    }

    // 通过链接助力
    public void completeAssist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // 获取当前助力人ID
        Integer userId = user.getId();

        JSONObject jsonResponse = JsonUtil.getJsonObject(request);

        // 获取参数
        String inviterIdStr = request.getParameter("inviterId");
        String invitedIdStr = request.getParameter("friendId");
        String assistToken = request.getParameter("token");

        if (inviterIdStr == null || assistToken == null) {
            response.getWriter().write("参数缺失");
            return;
        }

        int inviterId = Integer.parseInt(inviterIdStr);
        int invitedId = Integer.parseInt(invitedIdStr);
        if (couponService.assistByLinkAndCreateCoupon(inviterId,invitedId ,assistToken)) {
            response.getWriter().write("成功");
        } else {
            response.getWriter().write("失败");
        }
    }

    public void getAvailableCoupons(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        List<Coupon> availableCoupons = couponService.getAvailableCoupons(user.getId());
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", true);
        jsonResponse.put("message", "获取可用优惠券成功");
        jsonResponse.put("coupons", availableCoupons);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void useCoupon(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        JSONObject jsonResponse = JsonUtil.getJsonObject(request);
        int couponId = jsonResponse.getInteger("couponId");
        if (couponService.useCoupon(couponId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "使用优惠券成功");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "使用优惠券失败");
        }
        response.getWriter().write(jsonResponse.toJSONString());

    }
}
