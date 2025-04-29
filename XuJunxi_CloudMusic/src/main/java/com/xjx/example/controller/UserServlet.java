package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.User;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.UserServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();

    public void register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String checkCode = request.getParameter("checkCode");
        String checkCodeGen = (String) request.getSession().getAttribute("checkCodeGen");
        String adminInvitationCode = request.getParameter("adminInvitationCode"); // 获取管理员邀请码
        System.out.println("用户注册信息: " + username + ", " + email + ", " + password + ", " + confirmPassword + ", " + checkCode);

        JSONObject jsonResponse = new JSONObject();

        if (!password.equals(confirmPassword)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "两次密码输入不一致");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        if (!checkCode.equalsIgnoreCase(checkCodeGen)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "验证码错误");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        boolean success;
        if (adminInvitationCode != null) {
            // 如果管理员邀请码不为空，则调用管理员注册方法
            System.out.println("管理员邀请码：" + adminInvitationCode);
            success = userService.adminRegister(user, adminInvitationCode);
            if (success) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "注册成功");
                response.getWriter().write(jsonResponse.toJSONString());
            }else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "管理员邀请码错误");
                response.getWriter().write(jsonResponse.toJSONString());
            }
        } else {
            // 调用普通注册方法
            success = userService.register(user);
            if (success) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "注册成功");
                response.getWriter().write(jsonResponse.toJSONString());
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "注册失败，用户名或邮箱已被占用");
                response.getWriter().write(jsonResponse.toJSONString());
            }
        }
    }

    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应的字符编码和内容类型
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");
        String checkCode = request.getParameter("checkCode");
        String checkCodeGen = (String) request.getSession().getAttribute("checkCodeGen");
        JSONObject jsonResponse = new JSONObject();
        if (!checkCode.equalsIgnoreCase(checkCodeGen)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "验证码错误");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }
        if ("true".equals(remember)) {
            // 如果用户选择了记住密码，将用户信息保存到会话中
            request.getSession().setAttribute("remember", true);
        } else {
            // 如果用户没有选择记住密码，将用户信息从会话中移除
            request.getSession().removeAttribute("remember");
        }
        User user = userService.login(username, password);

        if (user != null) {
            // 将用户信息保存到会话中
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            jsonResponse.put("isAdmin", user.getRole());
            jsonResponse.put("success", true);
            jsonResponse.put("message", "登录成功");
            response.getWriter().write(jsonResponse.toJSONString());
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "用户名或密码不正确");
            response.getWriter().write(jsonResponse.toJSONString());
        }
    }

    public void getAllUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<User> users = userService.getAllUsers();
        response.getWriter().write(users.toString());
    }

    public void getUserById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        User user = userService.getUserById(id);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        JSONObject userJson = new JSONObject();
        if (user != null) {
            userJson.put("success", true);
            userJson.put("message", "获取用户信息成功");
            userJson.put("user", user);
        } else {
            userJson.put("success", false);
            userJson.put("message", "用户不存在");
        }
        response.getWriter().write(userJson.toJSONString());
    }

    public void displayPersonalInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            if (user.getPhone() == null){
                user.setPhone("暂无");
            }
            if (user.getAvatar() == null){
                user.setAvatar("暂未设置");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createdTime = user.getCreatedAt().format(formatter);
            JSONObject userJson = new JSONObject();
            userJson.put("user",user);
            userJson.put("createdTime",createdTime);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(userJson.toJSONString());
        } else {
            // 未登录或会话已过期，返回401状态码和错误信息
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未登录或会话已过期");
        }
    }

    public void updateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        if (user != null) {
            user.setUsername(username);
            user.setEmail(email);
            boolean success = userService.updateUser(user);
            response.getWriter().write(success ? "更新成功" : "更新失败");
        } else {
            response.getWriter().write("用户不存在");
        }
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = userService.deleteUser(id);
        response.getWriter().write(success ? "删除成功" : "删除失败");
    }

    public void resetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        JSONObject jsonResponse = new JSONObject();
        // 设置响应的字符编码和内容类型
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (!newPassword.equals(confirmPassword)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "两次密码输入不一致");
            response.getWriter().write(jsonResponse.toJSONString());
            return;
        }
        boolean success = userService.resetPassword(email, newPassword);
        if (success) {
            jsonResponse.put("success", true);
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "输入的邮箱未绑定该账户");
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonResponse.toJSONString());
    }
}

