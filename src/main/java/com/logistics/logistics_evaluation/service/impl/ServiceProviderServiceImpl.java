package com.logistics.logistics_evaluation.service.impl;

import com.logistics.logistics_evaluation.entity.Review;
import com.logistics.logistics_evaluation.entity.ServiceProvider;
import com.logistics.logistics_evaluation.repository.ReviewRepository;
import com.logistics.logistics_evaluation.repository.ServiceProviderRepository;
import com.logistics.logistics_evaluation.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceProviderServiceImpl implements ServiceProviderService {
    private final ServiceProviderRepository providerRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public List<ServiceProvider> getAllProviders() {
        return providerRepository.findAll();
    }

    @Override
    public ServiceProvider getProviderById(Integer id) {
        return providerRepository.findById(id).orElse(null);
    }

    @Override
    public void addProvider(ServiceProvider provider) {
        providerRepository.save(provider);
    }

    @Override
    public void updateProvider(ServiceProvider provider) {
        providerRepository.save(provider);
    }

    @Override
    public boolean deleteProvider(Integer id) {
        if (providerRepository.existsById(id)) {
            providerRepository.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    public List<ServiceProvider> getProvidersByOverallScore() {
        return providerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ServiceProvider::getAvgScore,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getProvidersByTimeliness() {
        return getProvidersByDimension("timeliness");
    }

    @Override
    public List<Map<String, Object>> getProvidersByAttitude() {
        return getProvidersByDimension("attitude");
    }

    @Override
    public List<Map<String, Object>> getProvidersByIntegrity() {
        return getProvidersByDimension("integrity");
    }

    @Override
    public List<Map<String, Object>> getProvidersByPrice() {
        return getProvidersByDimension("price");
    }


    private List<Map<String, Object>> getProvidersByDimension(String dimension) {
        // 计算所有服务商的单维度平均分
        Map<Integer, Double> scoreMap = new HashMap<>();
        Map<Integer, Integer> countMap = new HashMap<>();
        List<Review> allReviews = reviewRepository.findAll();
        for (Review r : allReviews) {
            int pid = r.getProviderId();
            double val = switch (dimension) {
                case "timeliness" -> r.getTimelinessScore();
                case "attitude" -> r.getAttitudeScore();
                case "integrity" -> r.getIntegrityScore();
                case "price" -> r.getPriceScore();
                default -> 0;
            };
            scoreMap.merge(pid, val, Double::sum);
            countMap.merge(pid, 1, Integer::sum);
        }
        // 获取所有服务商，填充分数
        return providerRepository.findAll().stream().map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getProviderId());
                    map.put("name", p.getName());
                    int cnt = countMap.getOrDefault(p.getProviderId(), 0);
                    double avg = cnt > 0 ? scoreMap.get(p.getProviderId()) / cnt : 0;
                    map.put("score", Math.round(avg * 10.0) / 10.0); // 保留一位小数
                    map.put("reviewCount", cnt);
                    return map;
                }).sorted((m1, m2) -> Double.compare((Double) m2.get("score"), (Double) m1.get("score")))
                .collect(Collectors.toList());
    }
}