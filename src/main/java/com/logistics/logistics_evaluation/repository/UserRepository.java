package com.logistics.logistics_evaluation.repository;

import com.logistics.logistics_evaluation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Integer> {
    List<User> findByUsernameContaining(String username);
    Optional<User> findByUsername(String username);   // 用于登录查询
}
