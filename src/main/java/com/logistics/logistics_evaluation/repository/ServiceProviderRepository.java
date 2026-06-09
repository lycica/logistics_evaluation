package com.logistics.logistics_evaluation.repository;

import com.logistics.logistics_evaluation.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider,Integer> {
    List<ServiceProvider> findByNameContaining(String keyword);
}
