package com.logistics.logistics_evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId")
    private Integer reviewId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "provider_id", nullable = false)
    private Integer providerId;

    @Column(name = "timeliness_score",nullable = false)
    private Integer timelinessScore;          // 时效性评分

    @Column(name = "attitude_score",nullable = false)
    private Integer attitudeScore;       // 服务态度评分

    @Column(name = "integrity_score",nullable = false)
    private Integer integrityScore;      // 包裹完好度评分

    @Column(name = "price_score",nullable = false)
    private Integer priceScore;          // 价格评分

    @Column(name = "comment_text", columnDefinition = "TEXT")
    private String commentText;              // 文字评论

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
