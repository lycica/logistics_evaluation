package com.logistics.logistics_evaluation.service;

import com.logistics.logistics_evaluation.entity.Review;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    /** 提交评价（含图片） */
    Review submitReview(Integer userId, Integer providerId,
                        Integer timeliness, Integer attitude,
                        Integer integrity, Integer price,
                        String commentText, List<MultipartFile> imageFiles);

    /** 获取某用户的所有评价 */
    List<Review> getReviewsByUser(Integer userId);
    List<Review> getAllReviews();
    boolean deleteReviewByAdmin(Integer reviewId);
    /** 获取某服务商的所有评价 */
    List<Review> getReviewsByProvider(Integer providerId);

    /** 修改评价（用户只能修改自己的，且需校验一致性） */
    boolean updateReview(Integer reviewId, Integer userId,
                         Integer timeliness, Integer attitude,
                         Integer integrity, Integer price,
                         String commentText, List<MultipartFile> imageFiles);

    /** 删除评价（同时删除关联图片文件和数据库记录） */
    boolean deleteReview(Integer reviewId, Integer userId);
    long getTotalReviewCount();
    long getDistinctUserCount();
    double getOverallAvgScore();
    Map<Integer, Map<String, Double>> getProviderScores(); // 每个服务商的各维度平均分
}