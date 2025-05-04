package com.xjx.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xjx.example.entity.PageBean;
import com.xjx.example.entity.User;
import com.xjx.example.service.UserService;
import com.xjx.example.service.impl.UserServiceImpl;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private final UserService userService = new UserServiceImpl();
    private static final String UPLOAD_DIRECTORY = "uploads"; // 上传文件的目录
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB 内存阈值
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB 单个文件最大大小
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB 整个请求最大大小
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
        User user = userService.login(username, password);
        if (user != null) {
            if ("true".equals(remember)) {
                // 如果用户选择了记住我，则设置Cookie的过期时间为一周

                Cookie usernameCookie = new Cookie("username", URLEncoder.encode(username, "UTF-8"));
                Cookie passwordCookie = new Cookie("password", password);
                Cookie rememberCookie = new Cookie("remember", "true");
                // 设置 Cookie 属性
                usernameCookie.setPath("/");
                passwordCookie.setPath("/");
                rememberCookie.setPath("/");
                usernameCookie.setMaxAge(7 * 24 * 60 * 60);
                passwordCookie.setMaxAge(7 * 24 * 60 * 60);
                rememberCookie.setMaxAge(7 * 24 * 60 * 60);
                response.addCookie(usernameCookie);
                response.addCookie(passwordCookie);
                response.addCookie(rememberCookie);
            }else {
                // 清除Cookie
                Cookie usernameCookie = new Cookie("username", "");
                Cookie passwordCookie = new Cookie("password", "");
                Cookie rememberCookie = new Cookie("remember", "");
                usernameCookie.setPath("/");
                passwordCookie.setPath("/");
                rememberCookie.setPath("/");
                usernameCookie.setMaxAge(0);
                passwordCookie.setMaxAge(0);
                rememberCookie.setMaxAge(0);
                response.addCookie(usernameCookie);
                response.addCookie(passwordCookie);
                response.addCookie(rememberCookie);
            }

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
            JSONObject userJson = new JSONObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createdTime = user.getCreatedAt().format(formatter);

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

    public void searchUsersByUsername(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        JSONObject json = JSONObject.parseObject(line);
        String keyword = json.getString("keyword");
        int currentPage = json.getInteger("currentPage");
        int pageSize = json.getInteger("pageSize");
        PageBean<User> pageBean = userService.searchUsersByUsername(keyword, currentPage, pageSize);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", true);
        jsonResponse.put("message", "搜索成功");
        jsonResponse.put("pageBean", pageBean);
        response.getWriter().write(jsonResponse.toJSONString());
    }

    public void loginStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession(false);
        String error = (String) session.getAttribute("login_error");
        JSONObject json = new JSONObject();
        json.put("error", error);
        if (session != null && session.getAttribute("user") != null) {
            json.put("isLoggedIn", true);
        } else {
            json.put("isLoggedIn", false);
        }
        response.getWriter().write(json.toJSONString());
    }

    // 获取原始访问的URL
    public void getOriginalUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String originalUrl = (String) session.getAttribute("originalUrl");
        System.out.println("originalUrl: " + originalUrl);
        JSONObject json = new JSONObject();
        json.put("originalUrl", originalUrl);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json.toJSONString());
        // 删除session中的originalUrl，以确保只获取一次
        session.removeAttribute("originalUrl");
    }

    public void updatePhone(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        String newPhone = request.getParameter("phone");
        if (newPhone == null || newPhone.trim().isEmpty()) {
            JSONObject json = new JSONObject();
            json.put("success", false);
            json.put("message", "手机号不能为空");
            response.getWriter().write(json.toJSONString());
            return;
        }

        User user = (User) request.getSession().getAttribute("user");
        boolean success = userService.updatePhone(user, newPhone);
        JSONObject json = new JSONObject();
        if (success) {
            json.put("success", true);
            json.put("message", "手机号更新成功");
        } else {
            json.put("success", false);
            json.put("message", "手机号更新失败");
        }
        response.getWriter().write(json.toJSONString());
    }

    public void uploadAvatar(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 如果请求不是 multipart/form-data，则返回错误信息
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"请求不是 multipart/form-data\"}");
            out.flush();
            return;
        }

        // 配置上传设置
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存阈值，超过此值时将文件写入临时目录
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 设置临时目录
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 设置单个文件最大大小
        upload.setFileSizeMax(MAX_FILE_SIZE);
        // 设置整个请求最大大小
        upload.setSizeMax(MAX_REQUEST_SIZE);

        JSONObject jsonResponse = new JSONObject();

        try {
            List<FileItem> formItems = upload.parseRequest(request);
            if (formItems != null && formItems.size() > 0) {
                FileItem fileItem = null; // 存储文件项
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        fileItem = item;
                        break;
                    }
                }
                if (fileItem != null) {
                    String uploadPath = getServletContext().getRealPath("/img");
                    File uploadDir = new File(uploadPath);

                    // 如果目录不存在，则创建
                    if (!uploadDir.exists()) {
                        boolean created = uploadDir.mkdirs(); // 创建多级目录
                        if (!created) {
                            System.err.println("无法创建上传目录：" + uploadDir.getAbsolutePath());
                            jsonResponse.put("success", false);
                            jsonResponse.put("message", "无法创建上传目录");
                            response.getWriter().write(jsonResponse.toJSONString());
                            return;
                        }
                    }

                    // 获取原始文件名
                    String fileName = fileItem.getName();
                    if (fileName == null || fileName.isEmpty()) {
                        System.err.println("文件名为空");
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "文件名为空");
                        response.getWriter().write(jsonResponse.toJSONString());
                        return;
                    }

                    // 提取文件扩展名
                    String fileExt = "";
                    int dotIndex = fileName.lastIndexOf(".");
                    if (dotIndex > 0) {
                        fileExt = fileName.substring(dotIndex);
                    }

                    // 使用 UUID 避免重复文件名
                    String uniqueFileName = System.currentTimeMillis() + "_" + (int)(Math.random() * 10000) + fileExt;

                    // 构建目标文件对象
                    File storeFile = new File(uploadDir, uniqueFileName);

                    // 写入磁盘
                    try {
                        fileItem.write(storeFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        jsonResponse.put("success", false);
                        jsonResponse.put("message", "文件写入失败：" + e.getMessage());
                        response.getWriter().write(jsonResponse.toJSONString());
                        return;
                    }

                    // 构建文件的 URL
                    String avatarUrl = "/XuJunxi_CloudMusic/img/" + uniqueFileName;
                    User user = (User) request.getSession().getAttribute("user");
                    boolean success = userService.updateAvatar(user, avatarUrl);
                    JSONObject json = new JSONObject();
                    if (success) {
                        user.setAvatar(avatarUrl); // 更新 Session 中的用户信息
                        json.put("success", true);
                        json.put("message", "头像上传成功");
                    } else {
                        json.put("success", false);
                        json.put("message", "头像上传失败");
                    }
                    response.getWriter().write(json.toJSONString());
                } else {
                    // 如果文件不存在，则返回错误信息
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "该文件不存在");
                    response.getWriter().write(jsonResponse.toJSONString());
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }

}

