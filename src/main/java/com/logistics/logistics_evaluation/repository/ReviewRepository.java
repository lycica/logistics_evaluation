package com.logistics.logistics_evaluation.repository;

import com.logistics.logistics_evaluation.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Integer> {
    // 查询某服务商的所有评价
    List<Review> findByProviderId(Integer providerId);

    // 查询某用户的所有评价
    List<Review> findByUserId(Integer userId);

    // 计算某服务商的平均评分（四项平均的综合）
    @Query("SELECT AVG((r.timelinessScore + r.attitudeScore + r.integrityScore + r.priceScore)/4.0) " +
            "FROM Review r WHERE r.providerId = :providerId")
    Double getAverageScoreByProviderId(@Param("providerId") Integer providerId);
}
