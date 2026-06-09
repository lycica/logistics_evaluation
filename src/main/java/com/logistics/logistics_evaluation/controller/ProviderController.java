package com.logistics.logistics_evaluation.controller;

import com.logistics.logistics_evaluation.entity.ServiceProvider;
import com.logistics.logistics_evaluation.entity.Review;
import com.logistics.logistics_evaluation.entity.User;
import com.logistics.logistics_evaluation.service.FavoriteService;
import com.logistics.logistics_evaluation.service.ServiceProviderService;
import com.logistics.logistics_evaluation.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/providers")
public class ProviderController {

    @Autowired
    private ServiceProviderService providerService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private ReviewService reviewService;

    // 服务商列表
    @GetMapping
    public String listProviders(Model model) {
        List<ServiceProvider> providers = providerService.getAllProviders();
        model.addAttribute("providers", providers);
        return "provider/list";
    }

    // 服务商详情（含评价列表）
    @GetMapping("/{providerId}")
    public String providerDetail(@PathVariable Integer providerId, Model model, HttpSession session) {
        ServiceProvider provider = providerService.getProviderById(providerId);
        if (provider == null) return "redirect:/providers";

        // 检查收藏状态
        User user = (User) session.getAttribute("currentUser");
        boolean isFavorite = false;
        if (user != null) {
            isFavorite = favoriteService.isFavorite(user.getUserId(), providerId);
        }

        List<Review> reviews = reviewService.getReviewsByProvider(providerId);
        model.addAttribute("provider", provider);
        model.addAttribute("reviews", reviews);
        model.addAttribute("isFavorite", isFavorite);
        return "provider/detail";
    }

    @PostMapping("/favorite/toggle")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@RequestParam Integer providerId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        boolean isFav = favoriteService.isFavorite(user.getUserId(), providerId);
        if (isFav) {
            favoriteService.removeFavorite(user.getUserId(), providerId);
            result.put("favorited", false);
        } else {
            favoriteService.addFavorite(user.getUserId(), providerId);
            result.put("favorited", true);
        }
        result.put("success", true);
        return result;
    }
}