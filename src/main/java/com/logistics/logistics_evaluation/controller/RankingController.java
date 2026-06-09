package com.logistics.logistics_evaluation.controller;

import com.logistics.logistics_evaluation.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class RankingController {
    @Autowired
    private ServiceProviderService providerService;

    @GetMapping("/rankings")
    public String rankings(Model model) {
        // 1. 综合排行
        model.addAttribute("overall", providerService.getProvidersByOverallScore());
        model.addAttribute("timelinessList", providerService.getProvidersByTimeliness());
        model.addAttribute("attitudeList", providerService.getProvidersByAttitude());
        model.addAttribute("integrityList", providerService.getProvidersByIntegrity());
        model.addAttribute("priceList", providerService.getProvidersByPrice());
        return "rankings";
    }
}