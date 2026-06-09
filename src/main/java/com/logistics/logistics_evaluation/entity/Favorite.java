package com.logistics.logistics_evaluation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorite")
@Data

public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Column(name = "provider_id", nullable = false)
    private Integer providerId;

    @Column(name = "favorite_time")
    private LocalDateTime favoriteTime;

    @PrePersist
    public void prePersist() {
        favoriteTime = LocalDateTime.now();
    }
}
