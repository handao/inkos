package com.inkos.repository;

import com.inkos.entity.LlmServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LlmServiceConfigRepository extends JpaRepository<LlmServiceConfig, Long> {
    List<LlmServiceConfig> findByUserId(Long userId);
}
