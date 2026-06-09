package com.logistics.logistics_evaluation.service.impl;

import com.logistics.logistics_evaluation.entity.Favorite;
import com.logistics.logistics_evaluation.entity.ServiceProvider;
import com.logistics.logistics_evaluation.repository.FavoriteRepository;
import com.logistics.logistics_evaluation.repository.ServiceProviderRepository;
import com.logistics.logistics_evaluation.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ServiceProviderRepository providerRepository;

    @Override
    @Transactional
    public boolean addFavorite(Integer userId, Integer providerId) {
        if (favoriteRepository.findByUserIdAndProviderId(userId, providerId).isPresent()) {
            return false;   // 已收藏
        }
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setProviderId(providerId);
        favoriteRepository.save(fav);
        return true;
    }

    @Override
    @Transactional
    public boolean removeFavorite(Integer userId, Integer providerId) {
        if (favoriteRepository.findByUserIdAndProviderId(userId, providerId).isEmpty()) {
            return false;   // 未收藏
        }
        favoriteRepository.deleteByUserIdAndProviderId(userId, providerId);
        return true;
    }

    @Override
    public boolean isFavorite(Integer userId, Integer providerId) {
        return favoriteRepository.findByUserIdAndProviderId(userId, providerId).isPresent();
    }

    @Override
    public List<ServiceProvider> getUserFavorites(Integer userId) {
        List<Favorite> favs = favoriteRepository.findByUserId(userId);
        List<Integer> providerIds = favs.stream()
                .map(Favorite::getProviderId)
                .collect(Collectors.toList());
        return providerRepository.findAllById(providerIds);
    }
}