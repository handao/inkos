package com.inkos.repository;

import com.inkos.entity.AgentMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<AgentMessage, Long> {
    List<AgentMessage> findBySessionIdOrderBySortOrderAsc(String sessionId);
}
