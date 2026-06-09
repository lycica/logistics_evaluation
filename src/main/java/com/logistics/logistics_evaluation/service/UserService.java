package com.logistics.logistics_evaluation.service;

import com.logistics.logistics_evaluation.entity.User;

import java.util.List;

public interface UserService {
    boolean register(User user);
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 明文密码
     * @return 验证成功返回 User 对象，失败返回 null
     */
    User login(String username,String password);
    List<User> getAllUsers();
    void deleteUser(Integer userId);
}
