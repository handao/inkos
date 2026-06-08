package com.inkos.dto.response;
import com.inkos.entity.User;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @Builder
public class AdminUserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String role;
    private final String status;
    private final LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;

    public static AdminUserResponse from(User user) {
        return AdminUserResponse.builder()
                .id(user.getId()).email(user.getEmail())
                .nickname(user.getNickname()).role(user.getRole())
                .status(user.getStatus()).lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt()).build();
    }
}
