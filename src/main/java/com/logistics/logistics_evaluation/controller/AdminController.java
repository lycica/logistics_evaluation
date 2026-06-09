package com.logistics.logistics_evaluation.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.logistics.logistics_evaluation.entity.Review;
import com.logistics.logistics_evaluation.entity.ServiceProvider;
import com.logistics.logistics_evaluation.entity.User;
import com.logistics.logistics_evaluation.repository.UserRepository;
import com.logistics.logistics_evaluation.service.ReviewService;
import com.logistics.logistics_evaluation.service.ServiceProviderService;
import com.logistics.logistics_evaluation.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;   // final
    private final UserService userService;
    private final ReviewService reviewService;
    @Autowired
    private ServiceProviderService providerService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // ====================== 服务商列表 ======================
    @GetMapping("/providers")
    public String listProviders(Model model) {
        model.addAttribute("providers", providerService.getAllProviders());
        return "admin/provider-list";
    }

    // ====================== 新增服务商表单 ======================
    @GetMapping("/providers/add")
    public String showAddForm(Model model) {
        model.addAttribute("provider", new ServiceProvider());
        model.addAttribute("isEdit", false);   // 标识为新增模式
        return "admin/provider-form";
    }

    // ====================== 编辑服务商表单 ======================
    @GetMapping("/providers/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        ServiceProvider provider = providerService.getProviderById(id);
        if (provider == null) {
            return "redirect:/admin/providers";
        }
        model.addAttribute("provider", provider);
        model.addAttribute("isEdit", true);    // 标识为编辑模式
        return "admin/provider-form";
    }

    // ====================== 保存服务商（新增/更新） ======================
    @PostMapping("/providers/save")
    public String saveProvider(@Valid @ModelAttribute("provider") ServiceProvider provider,
                               BindingResult bindingResult,
                               @RequestParam(defaultValue = "false") boolean isEdit,
                               Model model) {
        // 基础校验：名称必填
        if (provider.getName() == null || provider.getName().trim().isEmpty()) {
            bindingResult.rejectValue("name", "error.provider", "服务商名称不能为空");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/provider-form";
        }

        if (isEdit) {
            // 更新时需要从数据库获取原有评分等信息，避免覆盖
            ServiceProvider existing = providerService.getProviderById(provider.getProviderId());
            if (existing != null) {
                existing.setName(provider.getName());
                existing.setContact(provider.getContact());
                // avgScore 和 reviewCount 由系统计算，不在此修改
                providerService.updateProvider(existing);
            }
        } else {
            // 新增服务商，平均分和评价数初始为0
            provider.setAvgScore(java.math.BigDecimal.ZERO);
            provider.setReviewCount(0);
            providerService.addProvider(provider);
        }
        return "redirect:/admin/providers";
    }

    // ====================== 删除服务商 ======================
    @GetMapping("/providers/delete/{id}")
    public String deleteProvider(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        boolean success = providerService.deleteProvider(id);
        if (!success) {
            redirectAttributes.addFlashAttribute("error", "删除失败，服务商不存在或有关联评价无法删除。");
        } else {
            redirectAttributes.addFlashAttribute("message", "删除成功！");
        }
        return "redirect:/admin/providers";
    }
    // ====================== 用户管理 ======================
    @GetMapping("/users")
    public String listUsers(@RequestParam(required = false) String keyword, Model model) {
        List<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 按用户名模糊搜索（需在 UserRepository 添加方法）
            users = userRepository.findByUsernameContaining(keyword.trim());
        } else {
            users = userService.getAllUsers();
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "admin/users";
    }

    @GetMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Integer userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("message", "用户删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败，可能有关联数据");
        }
        return "redirect:/admin/users";
    }

    // ====================== 评价管理 ======================
    @GetMapping("/reviews")
    public String listReviews(@RequestParam(required = false) Integer providerId,
                              @RequestParam(required = false) Integer minScore,
                              @RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        // 可以在 Service 中加筛选方法，此处简单过滤演示
        if (providerId != null) {
            reviews = reviews.stream().filter(r -> r.getProviderId().equals(providerId)).collect(Collectors.toList());
        }
        model.addAttribute("reviews", reviews);
        return "admin/reviews";
    }

    @GetMapping("/reviews/delete/{reviewId}")
    public String deleteReview(@PathVariable Integer reviewId, RedirectAttributes redirectAttributes) {
        boolean ok = reviewService.deleteReviewByAdmin(reviewId);
        if (ok) {
            redirectAttributes.addFlashAttribute("message", "评价已删除");
        } else {
            redirectAttributes.addFlashAttribute("error", "删除失败，评价不存在");
        }
        return "redirect:/admin/reviews";
    }
    @GetMapping("/statistics")
    public String statistics(Model model) throws JsonProcessingException {
        // 概览指标
        model.addAttribute("totalReviews", reviewService.getTotalReviewCount());
        model.addAttribute("totalUsers", reviewService.getDistinctUserCount());
        model.addAttribute("totalProviders", providerService.getAllProviders().size());
        model.addAttribute("overallAvg", String.format("%.2f", reviewService.getOverallAvgScore()));

        // 图表数据
        Map<Integer, Map<String, Double>> scores = reviewService.getProviderScores();
        Map<Integer, String> names = providerService.getAllProviders().stream()
                .collect(Collectors.toMap(ServiceProvider::getProviderId, ServiceProvider::getName));
        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("scoresJson", mapper.writeValueAsString(scores));
        model.addAttribute("namesJson", mapper.writeValueAsString(names));
        return "admin/statistics";   // 返回模板路径
    }
}