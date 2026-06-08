package com.inkos.repository;

import com.inkos.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(String email, String type);
    void deleteByExpiresAtBefore(LocalDateTime time);
}
