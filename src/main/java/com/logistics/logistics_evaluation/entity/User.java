package com.logistics.logistics_evaluation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "user")  //指定对应的数据库表名
@Data
public class User {
    @Id  // 标记主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需在3-20个字符之间")
    @Column(name = "username",nullable = false)
    private String username;

    @NotBlank(message = "密码不能为空")
    // 不能在这里校验长度，因为存入的是加密后的密码，长度肯定超 @Size(min = 6, max = 20, message = "密码长度需在6-20个字符之间")
    @Column(name = "password",nullable = false)
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(name = "email")
    private String email;

    @Column(name = "role", columnDefinition = "ENUM('user','admin') DEFAULT 'user'")
    private String role;       // "user" 或 "admin"
    // 这里不需要校验 role，因为它不由用户输入
}
