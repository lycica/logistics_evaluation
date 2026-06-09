package com.logistics.logistics_evaluation.controller;

import com.logistics.logistics_evaluation.entity.Review;
import com.logistics.logistics_evaluation.entity.User;
import com.logistics.logistics_evaluation.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    // ========== 提交评价页面 ==========
    @GetMapping("/write/{providerId}")
    public String showWriteReview(@PathVariable Integer providerId, Model model) {
        model.addAttribute("providerId", providerId);
        model.addAttribute("review", new Review());
        return "review/review-form";
    }
    // ========== 处理评价提交 ==========
    @PostMapping("/write")
    public String submitReview(@RequestParam Integer providerId,
                               @RequestParam Integer timeliness,
                               @RequestParam Integer attitude,
                               @RequestParam Integer integrity,
                               @RequestParam Integer price,
                               @RequestParam(required = false) String commentText,
                               @RequestParam(required = false) List<MultipartFile> imageFiles,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/user/login";

        try {
            reviewService.submitReview(user.getUserId(), providerId,
                    timeliness, attitude, integrity, price,
                    commentText, imageFiles);
            redirectAttributes.addFlashAttribute("message", "评价发表成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "发表失败：" + e.getMessage());
        }
        return "redirect:/providers/" + providerId;
    }

    // ========== 我的评价列表 ==========
    @GetMapping("/my")
    public String myReviews(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/user/login";

        List<Review> reviews = reviewService.getReviewsByUser(user.getUserId());
        model.addAttribute("reviews", reviews);
        return "review/my-reviews";
    }

    // ========== 修改评价页面 ==========
    @GetMapping("/edit/{reviewId}")
    public String showEditReview(@PathVariable Integer reviewId,
                                 HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/user/login";

        // 验证评价是否属于该用户
        Review review = reviewService.getReviewsByUser(user.getUserId()).stream()
                .filter(r -> r.getReviewId().equals(reviewId))
                .findFirst().orElse(null);
        if (review == null) {
            model.addAttribute("error", "无权修改或评价不存在");
            return "redirect:/reviews/my";
        }
        model.addAttribute("review", review);
        return "review/review-form";
    }

    // ========== 处理修改评价 ==========
    @PostMapping("/edit")
    public String updateReview(@RequestParam Integer reviewId,
                               @RequestParam Integer providerId,
                               @RequestParam Integer timeliness,
                               @RequestParam Integer attitude,
                               @RequestParam Integer integrity,
                               @RequestParam Integer price,
                               @RequestParam(required = false) String commentText,
                               @RequestParam(required = false) List<MultipartFile> imageFiles,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/user/login";

        boolean success = reviewService.updateReview(reviewId, user.getUserId(),
                timeliness, attitude, integrity, price, commentText, imageFiles);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "评价修改成功！");
        } else {
            redirectAttributes.addFlashAttribute("error", "修改失败，无权操作或评价不存在");
        }
        return "redirect:/reviews/my";
    }

    // ========== 删除评价 ==========
    @GetMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Integer reviewId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/user/login";

        boolean success = reviewService.deleteReview(reviewId, user.getUserId());
        if (success) {
            redirectAttributes.addFlashAttribute("message", "评价已删除");
        } else {
            redirectAttributes.addFlashAttribute("error", "删除失败");
        }
        return "redirect:/reviews/my";
    }
}