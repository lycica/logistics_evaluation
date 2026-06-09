package com.logistics.logistics_evaluation.service.impl;

import com.logistics.logistics_evaluation.entity.User;
import com.logistics.logistics_evaluation.repository.UserRepository;
import com.logistics.logistics_evaluation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean register(User user) {
        // 1. 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return false;   // 用户名已被占用
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user");
        userRepository.save(user);
        return true;
    }

    @Override
    public User login(String username,String password) {
        // 1. 根据用户名查找用户
        User user =userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return null;//用户不存在
        }
        //密码
        if (passwordEncoder.matches(password,user.getPassword())) {
            return user;
        }
        return null;//密码错误
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}