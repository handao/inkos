package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.CreateSessionRequest;
import com.inkos.dto.request.UpdateSessionRequest;
import com.inkos.dto.response.MessageResponse;
import com.inkos.dto.response.SessionDetailResponse;
import com.inkos.dto.response.SessionResponse;
import com.inkos.entity.AgentSession;
import com.inkos.security.UserContext;
import com.inkos.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @GetMapping
    public ApiResponse<List<SessionResponse>> listSessions(
            @RequestParam(required = false) String bookId,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(sessionService.listSessions(user.getUserId(), bookId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(sessionService.createSession(request, user.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<SessionDetailResponse> getSession(
            @PathVariable String id,
            @AuthenticationPrincipal UserContext user) {
        List<MessageResponse> messages = sessionService.getMessages(id, user.getUserId());
        AgentSession session = sessionService.getSessionEntity(id, user.getUserId());
        return ApiResponse.success(SessionDetailResponse.from(session, messages));
    }

    @PutMapping("/{id}")
    public ApiResponse<SessionResponse> updateSession(
            @PathVariable String id,
            @Valid @RequestBody UpdateSessionRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(sessionService.updateSession(id, request, user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteSession(
            @PathVariable String id,
            @AuthenticationPrincipal UserContext user) {
        sessionService.deleteSession(id, user.getUserId());
        return ApiResponse.success();
    }
}
