package com.inkos.service;

import com.inkos.dto.request.*;
import com.inkos.dto.response.*;
import com.inkos.entity.*;
import com.inkos.exception.*;
import com.inkos.repository.*;
import com.inkos.security.JwtConfig;
import com.inkos.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AllowedEmailRepository allowedEmailRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtConfig jwtConfig;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void sendCode(String email) {
        verificationCodeRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, "REGISTER")
                .ifPresent(lastCode -> {
                    if (lastCode.getCreatedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
                        throw new BusinessException(ErrorCode.VERIFICATION_CODE_TOO_FREQUENT);
                    }
                });

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        List<AllowedEmail> whitelist = allowedEmailRepository.findAll();
        boolean allowed = whitelist.isEmpty() || whitelist.stream().anyMatch(w -> w.matches(email));
        if (!allowed) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_ALLOWED);
        }

        String code = String.format("%06d", secureRandom.nextInt(999999));
        VerificationCode vc = VerificationCode.builder()
                .email(email)
                .code(code)
                .type("REGISTER")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationCodeRepository.save(vc);
        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        VerificationCode vc = verificationCodeRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(request.getEmail(), "REGISTER")
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID));

        if (vc.isExpired()) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);
        }
        if (!vc.getCode().equals(request.getCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        vc.setUsed(true);
        verificationCodeRepository.save(vc);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("user")
                .status("active")
                .build();
        user = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtConfig.getAccessTokenExpiration())
                .user(UserInfoResponse.from(user))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtConfig.getAccessTokenExpiration())
                .user(UserInfoResponse.from(user))
                .build();
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtConfig.getAccessTokenExpiration())
                .user(UserInfoResponse.from(user))
                .build();
    }

    public UserInfoResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return UserInfoResponse.from(user);
    }
}
