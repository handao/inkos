package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.common.PagedResponse;
import com.inkos.dto.request.AddAllowedEmailRequest;
import com.inkos.dto.request.UpdateUserStatusRequest;
import com.inkos.dto.response.AdminUserResponse;
import com.inkos.entity.AllowedEmail;
import com.inkos.entity.User;
import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;
import com.inkos.repository.AllowedEmailRepository;
import com.inkos.repository.UserRepository;
import com.inkos.security.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;
    private final AllowedEmailRepository allowedEmailRepository;

    @GetMapping("/users")
    public ApiResponse<PagedResponse<AdminUserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        List<AdminUserResponse> content = userPage.getContent().stream()
                .map(AdminUserResponse::from)
                .toList();
        return ApiResponse.success(PagedResponse.<AdminUserResponse>builder()
                .content(content).page(page).size(size)
                .total(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build());
    }

    @PutMapping("/users/{id}/status")
    public ApiResponse<Void> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request,
            @AuthenticationPrincipal UserContext admin) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        user.setStatus(request.getStatus());
        userRepository.save(user);
        return ApiResponse.success();
    }

    @GetMapping("/allowed-emails")
    public ApiResponse<List<AllowedEmail>> listAllowedEmails() {
        return ApiResponse.success(allowedEmailRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping("/allowed-emails")
    public ApiResponse<AllowedEmail> addAllowedEmail(
            @Valid @RequestBody AddAllowedEmailRequest request,
            @AuthenticationPrincipal UserContext admin) {
        boolean exists = allowedEmailRepository.findAll().stream()
                .anyMatch(a -> a.getEmailPattern().equals(request.getEmailPattern()));
        if (exists) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该邮箱模式已存在");
        }
        AllowedEmail ae = AllowedEmail.builder()
                .emailPattern(request.getEmailPattern())
                .description(request.getDescription())
                .createdBy(admin.getUserId())
                .build();
        return ApiResponse.success(allowedEmailRepository.save(ae));
    }

    @DeleteMapping("/allowed-emails/{id}")
    public ApiResponse<Void> deleteAllowedEmail(@PathVariable Long id) {
        if (!allowedEmailRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        allowedEmailRepository.deleteById(id);
        return ApiResponse.success();
    }
}
