package com.logistics.logistics_evaluation.service;

import com.logistics.logistics_evaluation.entity.ServiceProvider;
import java.util.List;
import java.util.Map;

public interface ServiceProviderService {

    /** 获取所有服务商列表 */
    List<ServiceProvider> getAllProviders();

    /** 根据ID获取服务商 */
    ServiceProvider getProviderById(Integer id);

    /** 新增服务商 */
    void addProvider(ServiceProvider provider);

    /** 更新服务商 */
    void updateProvider(ServiceProvider provider);

    /** 删除服务商（注意：外键约束，如有关联评价则可能无法直接删除） */
    boolean deleteProvider(Integer id);

    List<ServiceProvider> getProvidersByOverallScore();

    List<Map<String, Object>> getProvidersByTimeliness();

    List<Map<String, Object>> getProvidersByAttitude();

    List<Map<String, Object>> getProvidersByIntegrity();

    List<Map<String, Object>> getProvidersByPrice();
}