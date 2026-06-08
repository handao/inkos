package com.inkos.service;

import com.inkos.dto.request.*;
import com.inkos.dto.response.*;
import com.inkos.entity.*;
import com.inkos.exception.*;
import com.inkos.repository.*;
import com.inkos.security.JwtConfig;
import com.inkos.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private VerificationCodeRepository verificationCodeRepository;
    @Mock private AllowedEmailRepository allowedEmailRepository;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private EmailService emailService;

    private JwtConfig jwtConfig;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        jwtConfig.setAccessTokenExpiration(900_000);
        jwtConfig.setRefreshTokenExpiration(604_800_000);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(
                userRepository, verificationCodeRepository, allowedEmailRepository,
                passwordEncoder, jwtTokenProvider, jwtConfig, emailService);
    }

    @Test
    void sendCode_shouldThrowWhenEmailAlreadyRegistered() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> authService.sendCode("test@test.com"));
    }

    @Test
    void sendCode_shouldThrowWhenEmailNotInWhitelist() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(allowedEmailRepository.findAll()).thenReturn(List.of(
                AllowedEmail.builder().emailPattern("@allowed.com").build()));
        assertThrows(BusinessException.class,
                () -> authService.sendCode("test@test.com"));
    }

    @Test
    void sendCode_shouldPassWhenWhitelistEmpty() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(allowedEmailRepository.findAll()).thenReturn(List.of());
        doNothing().when(emailService).sendVerificationCode(anyString(), anyString());
        assertDoesNotThrow(() -> authService.sendCode("test@test.com"));
        verify(verificationCodeRepository).save(any(VerificationCode.class));
    }

    @Test
    void register_shouldThrowWhenCodeInvalid() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setCode("wrong");
        req.setPassword("Pass1234");
        req.setNickname("Test");
        when(verificationCodeRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc("test@test.com", "REGISTER"))
                .thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> authService.register(req));
    }

    @Test
    void register_shouldSucceed() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setCode("123456");
        req.setPassword("Pass1234");
        req.setNickname("Test");
        VerificationCode vc = VerificationCode.builder()
                .email("test@test.com").code("123456").type("REGISTER")
                .expiresAt(LocalDateTime.now().plusMinutes(10)).build();
        when(verificationCodeRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc("test@test.com", "REGISTER"))
                .thenReturn(Optional.of(vc));
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(userRepository.save(any())).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateAccessToken(anyLong(), anyString(), anyString()))
                .thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(anyLong()))
                .thenReturn("refresh-token");
        AuthResponse response = authService.register(req);
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("Test", response.getUser().getNickname());
    }

    @Test
    void login_shouldSucceed() {
        User user = User.builder()
                .id(1L).email("test@test.com")
                .passwordHash(passwordEncoder.encode("pass123"))
                .nickname("Test").role("user").status("active").build();
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(1L, "test@test.com", "user"))
                .thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(1L)).thenReturn("refresh-token");
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass123");
        AuthResponse response = authService.login(req);
        assertEquals("access-token", response.getAccessToken());
    }
}
