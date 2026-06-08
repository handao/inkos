package com.inkos.repository;

import com.inkos.entity.AllowedEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AllowedEmailRepository extends JpaRepository<AllowedEmail, Long> {
    List<AllowedEmail> findAllByOrderByCreatedAtDesc();
}
