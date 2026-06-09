package com.logistics.logistics_evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "image")
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "image_path", length = 500, nullable = false)
    private String imagePath;                  // 图片存储路径或URL
}