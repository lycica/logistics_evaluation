package com.logistics.logistics_evaluation.controller;

import com.logistics.logistics_evaluation.entity.ServiceProvider;
import com.logistics.logistics_evaluation.entity.User;
import com.logistics.logistics_evaluation.service.FavoriteService;
import com.logistics.logistics_evaluation.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")   // 统一给本类所有方法加前缀 /user
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private FavoriteService favoriteService;
    // 显示注册页面
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());  // 绑定一个空对象给表单
        return "user/register";  // 对应 templates/user/register.html
    }

    // 处理注册表单提交
    @PostMapping("/register")
    public String doRegister(@Valid User user,        // @Valid 启用实体类校验（后续加）
                             BindingResult bindingResult,
                             Model model) {
        // 如果前端校验有错误（比如用户名格式不对），直接返回注册页显示错误
        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        // 手动校验密码长度（明文阶段）
        String rawPassword = user.getPassword();
        if (rawPassword == null || rawPassword.length() < 6 || rawPassword.length() > 20) {
            model.addAttribute("error", "密码长度需在6-20个字符之间");
            return "user/register";
        }

        // 检查邮箱是否为空字符串，转为 null 避免 @Email 校验空字符串
        if (user.getEmail() != null && user.getEmail().trim().isEmpty()) {
            user.setEmail(null);
        }

        // 调用 Service 进行注册
        boolean success = userService.register(user);
        if (success) {
            model.addAttribute("message", "注册成功！请登录。");
            return "user/login";   // 跳转到登录页，并显示提示信息
        } else {
            model.addAttribute("error", "用户名已被占用，请更换！");
            return "user/register";
        }
    }
    //登录
    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功：将用户信息存入 Session
            session.setAttribute("currentUser", user);
            // 根据角色跳转不同页面
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/dashboard";   // 管理员后台首页（稍后创建）
            } else {
                return "redirect:/";   // 普通用户首页（稍后创建）
            }
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "user/login";
        }
    }
    @GetMapping("/favorites")
    public String myFavorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/user/login";
        List<ServiceProvider> favorites = favoriteService.getUserFavorites(user.getUserId());
        model.addAttribute("favorites", favorites);
        return "favorite/list";
    }
    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // 销毁 Session
        return "redirect:/user/login";
    }
}