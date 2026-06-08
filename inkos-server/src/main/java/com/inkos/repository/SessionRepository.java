package com.inkos.repository;

import com.inkos.entity.AgentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<AgentSession, String> {
    List<AgentSession> findByUserIdAndBookIdOrderByUpdatedAtDesc(Long userId, String bookId);
    List<AgentSession> findByUserIdAndBookIdIsNullOrderByUpdatedAtDesc(Long userId);
}
