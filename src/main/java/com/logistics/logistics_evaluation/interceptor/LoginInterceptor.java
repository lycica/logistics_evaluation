package com.logistics.logistics_evaluation.interceptor;

import com.logistics.logistics_evaluation.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");
        String requestURI = request.getRequestURI();

        // 如果未登录
        if (user == null) {
            // 保存当前请求路径，登录后可以跳转回来
            session.setAttribute("redirectUrl", requestURI);
            response.sendRedirect("/user/login");
            return false;   // 中断请求
        }

        // 如果已登录，检查是否有权限访问管理员页面
        if (requestURI.startsWith("/admin") && !"admin".equals(user.getRole())) {
            response.sendRedirect("/");   // 普通用户企图访问管理后台，重定向到首页
            return false;
        }

        return true;  // 放行
    }
}