package com.logistics.logistics_evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "service_provider")
@Data

public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    private Integer providerId;                    // 服务商编号

    @Column(name = "name", nullable = false)
    private String name;                // 服务商名称

    @Column(name = "contact")
    private String contact;             // 联系方式

    @Column(name = "avg_score", precision = 5, scale = 2)
    private BigDecimal avgScore;            // 综合平均分

    @Column(name = "review_count")
    private Integer reviewCount;        // 评价总数
}
