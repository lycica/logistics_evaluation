package com.logistics.logistics_evaluation.service;
import com.logistics.logistics_evaluation.entity.ServiceProvider;
import java.util.List;

public interface FavoriteService {
    /** 添加收藏（返回是否新增成功，已存在则返回false） */
    boolean addFavorite(Integer userId, Integer providerId);

    /** 取消收藏 */
    boolean removeFavorite(Integer userId, Integer providerId);

    /** 检查是否已收藏 */
    boolean isFavorite(Integer userId, Integer providerId);

    /** 获取用户收藏的服务商列表 */
    List<ServiceProvider> getUserFavorites(Integer userId);
}