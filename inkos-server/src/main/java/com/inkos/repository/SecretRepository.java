package com.inkos.repository;

import com.inkos.entity.Secret;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SecretRepository extends JpaRepository<Secret, Long> {
    Optional<Secret> findByUserIdAndServiceKey(Long userId, String serviceKey);
    boolean existsByUserIdAndServiceKey(Long userId, String serviceKey);
}
