package com.xjx.example.filter;

import com.xjx.example.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String contextPath = request.getContextPath(); // 获取上下文路径
        String[] urls = new String[]{
                contextPath + "/login.html",
                contextPath + "/register.html",
                contextPath + "/adminRegister.html",
                contextPath + "/forgetPassword.html",
                contextPath + "/css/",
                contextPath + "/img/",
                contextPath + "/checkCodeServlet",
                contextPath + "/user/login",
                contextPath + "/user/register",
                contextPath + "/user/resetPassword",
                contextPath + "/user/loginStatus"
        };
        for (String url : urls) {
            if (request.getRequestURI().contains(url)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        // 如果用户已经登录，则放行
        if (user != null) {
            // 如果用户已登录，检查是否为管理员路径
            String requestURI = request.getRequestURI();
            if (requestURI.equals(contextPath + "/admin.html") ||
                    requestURI.startsWith(contextPath + "/admin/")) {
                if (user.getRole() != 2) {
                    // 非管理员访问管理页面，返回 403 拒绝访问
                    ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问管理员页面");
                    return;
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // 保存原始请求路径到 session
            String originalUrl = request.getRequestURI();

            session.setAttribute("originalUrl", originalUrl);
            request.getSession().setAttribute("login_error", "您尚未登录");
            request.getRequestDispatcher("/login.html").forward(request, servletResponse);
        }

    }

    @Override
    public void destroy() {}
}
