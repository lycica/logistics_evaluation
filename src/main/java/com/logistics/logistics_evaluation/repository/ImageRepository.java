package com.logistics.logistics_evaluation.repository;

import com.logistics.logistics_evaluation.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Integer> {
    List<Image> findByReviewId(Integer reviewId);
}
