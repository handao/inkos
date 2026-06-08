package com.inkos.service;

import com.inkos.dto.request.CreateSessionRequest;
import com.inkos.dto.request.UpdateSessionRequest;
import com.inkos.dto.response.MessageResponse;
import com.inkos.dto.response.SessionResponse;
import com.inkos.entity.AgentSession;
import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;
import com.inkos.repository.MessageRepository;
import com.inkos.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    public List<SessionResponse> listSessions(Long userId, String bookId) {
        List<AgentSession> sessions;
        if (bookId != null) {
            sessions = sessionRepository.findByUserIdAndBookIdOrderByUpdatedAtDesc(userId, bookId);
        } else {
            sessions = sessionRepository.findByUserIdAndBookIdIsNullOrderByUpdatedAtDesc(userId);
        }
        return sessions.stream().map(SessionResponse::from).toList();
    }

    public AgentSession getSessionEntity(String sessionId, Long userId) {
        AgentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return session;
    }

    @Transactional
    public SessionResponse createSession(CreateSessionRequest request, Long userId) {
        AgentSession session = AgentSession.builder()
                .sessionId(UUID.randomUUID().toString().toLowerCase())
                .userId(userId)
                .bookId(request.getBookId())
                .title(request.getTitle())
                .mode(request.getMode() != null ? request.getMode() : "chat")
                .isDraft(true)
                .build();
        return SessionResponse.from(sessionRepository.save(session));
    }

    @Transactional
    public SessionResponse updateSession(String sessionId, UpdateSessionRequest request, Long userId) {
        AgentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        if (request.getTitle() != null) session.setTitle(request.getTitle());
        if (request.getIsDraft() != null) session.setIsDraft(request.getIsDraft());
        if (request.getIsStreaming() != null) session.setIsStreaming(request.getIsStreaming());
        session.setIsDraft(false);
        return SessionResponse.from(sessionRepository.save(session));
    }

    @Transactional
    public void deleteSession(String sessionId, Long userId) {
        AgentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        sessionRepository.delete(session);
    }

    public List<MessageResponse> getMessages(String sessionId, Long userId) {
        AgentSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return messageRepository.findBySessionIdOrderBySortOrderAsc(sessionId)
                .stream().map(MessageResponse::from).toList();
    }
}
