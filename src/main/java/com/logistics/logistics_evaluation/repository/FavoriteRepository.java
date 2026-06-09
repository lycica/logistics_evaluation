package com.logistics.logistics_evaluation.repository;

import com.logistics.logistics_evaluation.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite,Integer> {
    List<Favorite> findByUserId(Integer userId);
    Optional<Favorite> findByUserIdAndProviderId(Integer userId, Integer providerId);
    void deleteByUserIdAndProviderId(Integer userId, Integer providerId);
}
