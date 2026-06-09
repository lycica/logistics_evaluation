package com.logistics.logistics_evaluation.service.impl;

import com.logistics.logistics_evaluation.entity.Image;
import com.logistics.logistics_evaluation.entity.Review;
import com.logistics.logistics_evaluation.entity.ServiceProvider;
import com.logistics.logistics_evaluation.repository.ImageRepository;
import com.logistics.logistics_evaluation.repository.ReviewRepository;
import com.logistics.logistics_evaluation.repository.ServiceProviderRepository;
import com.logistics.logistics_evaluation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j// 自动生成 log 对象
@Service
@RequiredArgsConstructor  // 为 final 字段生成构造器，实现构造器注入
public class ReviewServiceImpl implements ReviewService {

    // 依赖改为 final，通过构造器注入
    private final ReviewRepository reviewRepository;
    private final ImageRepository imageRepository;
    private final ServiceProviderRepository providerRepository;

    @Value("${app.upload.path}")
    private String uploadPath;

    // 敏感词列表
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "傻", "操", "妈的"
    );

    @Override
    @Transactional
    public Review submitReview(Integer userId, Integer providerId,
                               Integer timeliness, Integer attitude,
                               Integer integrity, Integer price,
                               String commentText, List<MultipartFile> imageFiles) {
        // 过滤敏感词
        commentText = filterSensitiveWords(commentText);

        // 保存评价
        Review review = new Review();
        review.setUserId(userId);
        review.setProviderId(providerId);
        review.setTimelinessScore(timeliness);
        review.setAttitudeScore(attitude);
        review.setIntegrityScore(integrity);
        review.setPriceScore(price);
        review.setCommentText(commentText);
        review = reviewRepository.save(review);  // 获取生成的 reviewId

        // 处理图片上传
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String path = saveImage(file);
                    Image img = new Image();
                    img.setReviewId(review.getReviewId());
                    img.setImagePath(path);
                    imageRepository.save(img);
                }
            }
        }

        // 更新服务商统计
        updateProviderStatistics(providerId);

        return review;
    }

    @Override
    public List<Review> getReviewsByUser(Integer userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public List<Review> getReviewsByProvider(Integer providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    @Override
    @Transactional
    public boolean updateReview(Integer reviewId, Integer userId,
                                Integer timeliness, Integer attitude,
                                Integer integrity, Integer price,
                                String commentText, List<MultipartFile> imageFiles) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) return false;
        Review review = optionalReview.get();
        if (!review.getUserId().equals(userId)) return false; // 只能改自己的

        // 更新评分
        review.setTimelinessScore(timeliness);
        review.setAttitudeScore(attitude);
        review.setIntegrityScore(integrity);
        review.setPriceScore(price);
        review.setCommentText(filterSensitiveWords(commentText));
        review.setUpdateTime(LocalDateTime.now());
        reviewRepository.save(review);

        // 处理图片：如果上传了新图片，删除旧图并保存新图
        if (imageFiles != null && !imageFiles.isEmpty()) {
            // 删除旧图片文件
            List<Image> oldImages = imageRepository.findByReviewId(reviewId);
            for (Image img : oldImages) {
                deleteImageFile(img.getImagePath());
                imageRepository.delete(img);
            }
            // 保存新图片
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String path = saveImage(file);
                    Image newImg = new Image();
                    newImg.setReviewId(reviewId);
                    newImg.setImagePath(path);
                    imageRepository.save(newImg);
                }
            }
        }

        // 更新服务商统计
        updateProviderStatistics(review.getProviderId());

        return true;
    }

    @Override
    @Transactional
    public boolean deleteReview(Integer reviewId, Integer userId) {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) return false;
        Review review = optionalReview.get();
        if (!review.getUserId().equals(userId)) return false;

        // 删除关联图片
        List<Image> images = imageRepository.findByReviewId(reviewId);
        for (Image img : images) {
            deleteImageFile(img.getImagePath());
            imageRepository.delete(img);
        }
        // 删除评价
        reviewRepository.deleteById(reviewId);
        // 更新服务商统计
        updateProviderStatistics(review.getProviderId());

        return true;
    }

    // ========== 私有辅助方法 ==========
    private String filterSensitiveWords(String text) {
        if (text == null) return null;
        String filtered = text;
        for (String word : SENSITIVE_WORDS) {
            filtered = filtered.replaceAll(word, "***");
        }
        return filtered;
    }

    // ========== 修改后的图片保存方法 ==========
    private String saveImage(MultipartFile file) {
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                log.error("无法创建上传目录: {}", dir.getAbsolutePath());
                throw new RuntimeException("服务器文件存储异常，请联系管理员");
            }
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String uniqueName = UUID.randomUUID() + extension;

        File dest = new File(dir, uniqueName);
        try {
            file.transferTo(dest);
            return "/upload/" + uniqueName;
        } catch (IOException e) {
            log.error("图片写入失败: {}", dest.getAbsolutePath(), e);
            throw new RuntimeException("图片上传失败", e);
        }
    }

    // 修改后的文件删除方法（增加失败日志）
    private void deleteImageFile(String imagePath) {
        if (imagePath == null) return;
        String realPath = uploadPath + imagePath.substring(imagePath.lastIndexOf("/") + 1);
        File file = new File(realPath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.warn("删除临时图片失败: {}", realPath);
            }
        }
    }

    private void updateProviderStatistics(Integer providerId) {
        // 计算该服务商所有评价的单项平均分，再计算综合平均分
        List<Review> reviews = reviewRepository.findByProviderId(providerId);
        int count = reviews.size();
        if (count == 0) {
            ServiceProvider provider = providerRepository.findById(providerId).orElse(null);
            if (provider != null) {
                provider.setAvgScore(BigDecimal.ZERO);
                provider.setReviewCount(0);
                providerRepository.save(provider);
            }
            return;
        }

        double sumTimeliness = 0, sumAttitude = 0, sumIntegrity = 0, sumPrice = 0;
        for (Review r : reviews) {
            sumTimeliness += r.getTimelinessScore();
            sumAttitude += r.getAttitudeScore();
            sumIntegrity += r.getIntegrityScore();
            sumPrice += r.getPriceScore();
        }
        // 四项平均分取整，保留两位小数
        double avgTimeliness = sumTimeliness / count;
        double avgAttitude = sumAttitude / count;
        double avgIntegrity = sumIntegrity / count;
        double avgPrice = sumPrice / count;
        double overallAvg = (avgTimeliness + avgAttitude + avgIntegrity + avgPrice) / 4.0;

        ServiceProvider provider = providerRepository.findById(providerId).orElse(null);
        if (provider != null) {
            provider.setAvgScore(BigDecimal.valueOf(overallAvg).setScale(2, RoundingMode.HALF_UP));
            provider.setReviewCount(count);
            providerRepository.save(provider);
        }
    }

    @Override
    public long getTotalReviewCount() {
        return reviewRepository.count();
    }

    @Override
    public long getDistinctUserCount() {
        return reviewRepository.findAll().stream()
                .map(Review::getUserId).distinct().count();
    }

    @Override
    public double getOverallAvgScore() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) return 0;
        double sum = reviews.stream()
                .mapToDouble(r -> (r.getTimelinessScore() + r.getAttitudeScore() +
                        r.getIntegrityScore() + r.getPriceScore()) / 4.0)
                .sum();
        return sum / reviews.size();
    }

    @Override
    public Map<Integer, Map<String, Double>> getProviderScores() {
        List<Review> reviews = reviewRepository.findAll();
        Map<Integer, List<Review>> grouped = reviews.stream()
                .collect(Collectors.groupingBy(Review::getProviderId));
        Map<Integer, Map<String, Double>> result = new HashMap<>();
        for (Map.Entry<Integer, List<Review>> e : grouped.entrySet()) {
            List<Review> list = e.getValue();
            double t = list.stream().mapToInt(Review::getTimelinessScore).average().orElse(0);
            double a = list.stream().mapToInt(Review::getAttitudeScore).average().orElse(0);
            double i = list.stream().mapToInt(Review::getIntegrityScore).average().orElse(0);
            double p = list.stream().mapToInt(Review::getPriceScore).average().orElse(0);
            Map<String, Double> scores = new HashMap<>();
            scores.put("timeliness", t);
            scores.put("attitude", a);
            scores.put("integrity", i);
            scores.put("price", p);
            scores.put("overall", (t + a + i + p) / 4.0);
            result.put(e.getKey(), scores);
        }
        List<ServiceProvider> allProviders = providerRepository.findAll();
        for (ServiceProvider sp : allProviders) {
            if (!result.containsKey(sp.getProviderId())) {
                Map<String, Double> zero = new HashMap<>();
                zero.put("timeliness", 0.0);
                zero.put("attitude", 0.0);
                zero.put("integrity", 0.0);
                zero.put("price", 0.0);
                zero.put("overall", 0.0);
                result.put(sp.getProviderId(), zero);
            }
        }
        return result;
    }
    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional
    public boolean deleteReviewByAdmin(Integer reviewId) {
        // 管理员强制删除评价，同时删除图片和更新统计
        Optional<Review> opt = reviewRepository.findById(reviewId);
        if (opt.isEmpty()) return false;
        Review review = opt.get();

        // 删除图片
        List<Image> images = imageRepository.findByReviewId(reviewId);
        for (Image img : images) {
            deleteImageFile(img.getImagePath());
            imageRepository.delete(img);
        }
        reviewRepository.deleteById(reviewId);
        updateProviderStatistics(review.getProviderId());
        return true;
    }
}