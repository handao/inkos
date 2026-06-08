package com.inkos.integration;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.*;
import com.inkos.dto.response.AuthResponse;
import com.inkos.entity.VerificationCode;
import com.inkos.repository.VerificationCodeRepository;
import com.inkos.service.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private VerificationCodeRepository verificationCodeRepository;

  @MockBean
  private EmailService emailService;

  private static final String PASSWORD = "Password123";

  private static int counter = 0;

  private String uniqueEmail() {
    return "test" + (++counter) + "@example.com";
  }

  @BeforeEach
  void setUp() {
    doNothing().when(emailService).sendVerificationCode(anyString(), anyString());
  }

  private int getCode(String json) throws Exception {
    return objectMapper.readTree(json).get("code").asInt();
  }

  private String getDataField(String json, String field) throws Exception {
    return objectMapper.readTree(json).path("data").path(field).asText();
  }

  @Test
  void testFullAuthFlow() throws Exception {
    String email = uniqueEmail();
    String nickname = "TestUser";

    // 1. Send verification code
    var sendReq = new SendCodeRequest();
    sendReq.setEmail(email);
    ResponseEntity<String> sendResp = restTemplate.postForEntity(
        "/api/v1/auth/send-code", sendReq, String.class);
    assertEquals(HttpStatus.OK, sendResp.getStatusCode());
    assertEquals(200, getCode(sendResp.getBody()));

    // 2. Retrieve verification code from DB
    VerificationCode vc = verificationCodeRepository
        .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, "REGISTER")
        .orElseThrow(() -> new AssertionError("Verification code not found in DB"));

    // 3. Register
    RegisterRequest registerReq = new RegisterRequest();
    registerReq.setEmail(email);
    registerReq.setCode(vc.getCode());
    registerReq.setPassword(PASSWORD);
    registerReq.setNickname(nickname);
    ResponseEntity<String> registerResp = restTemplate.postForEntity(
        "/api/v1/auth/register", registerReq, String.class);
    assertEquals(HttpStatus.CREATED, registerResp.getStatusCode());
    JsonNode registerData = objectMapper.readTree(registerResp.getBody()).path("data");
    assertFalse(registerData.path("accessToken").asText().isEmpty());
    assertFalse(registerData.path("refreshToken").asText().isEmpty());
    assertEquals(nickname, registerData.path("user").path("nickname").asText());

    String accessToken = registerData.path("accessToken").asText();

    // 4. Login
    LoginRequest loginReq = new LoginRequest();
    loginReq.setEmail(email);
    loginReq.setPassword(PASSWORD);
    ResponseEntity<String> loginResp = restTemplate.postForEntity(
        "/api/v1/auth/login", loginReq, String.class);
    assertEquals(HttpStatus.OK, loginResp.getStatusCode());
    JsonNode loginData = objectMapper.readTree(loginResp.getBody()).path("data");
    assertFalse(loginData.path("accessToken").asText().isEmpty());
    String refreshToken = loginData.path("refreshToken").asText();

    // 5. Refresh token
    RefreshTokenRequest refreshReq = new RefreshTokenRequest();
    refreshReq.setRefreshToken(refreshToken);
    ResponseEntity<String> refreshResp = restTemplate.postForEntity(
        "/api/v1/auth/refresh", refreshReq, String.class);
    assertEquals(HttpStatus.OK, refreshResp.getStatusCode());
    JsonNode refreshData = objectMapper.readTree(refreshResp.getBody()).path("data");
    assertFalse(refreshData.path("accessToken").asText().isEmpty());

    // 6. Get current user
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    ResponseEntity<String> meResp = restTemplate.exchange(
        "/api/v1/auth/me", HttpMethod.GET,
        new HttpEntity<>(headers), String.class);
    assertEquals(HttpStatus.OK, meResp.getStatusCode());
    JsonNode meData = objectMapper.readTree(meResp.getBody());
    assertEquals(200, meData.path("code").asInt());
    assertEquals(email, meData.path("data").path("email").asText());
  }

  @Test
  void testSendCode_shouldFailForAlreadyRegisteredEmail() throws Exception {
    String email = uniqueEmail();

    var sendReq = new SendCodeRequest();
    sendReq.setEmail(email);
    restTemplate.postForEntity("/api/v1/auth/send-code", sendReq, String.class);

    VerificationCode vc = verificationCodeRepository
        .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, "REGISTER")
        .orElseThrow(() -> new AssertionError("Verification code not found"));

    RegisterRequest registerReq = new RegisterRequest();
    registerReq.setEmail(email);
    registerReq.setCode(vc.getCode());
    registerReq.setPassword(PASSWORD);
    registerReq.setNickname("TestUser");
    restTemplate.postForEntity("/api/v1/auth/register", registerReq, String.class);

    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/send-code", sendReq, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    assertEquals(40901, getCode(resp.getBody()));
  }

  @Test
  void testRegister_shouldFailWithInvalidCode() throws Exception {
    RegisterRequest req = new RegisterRequest();
    req.setEmail(uniqueEmail());
    req.setCode("000000");
    req.setPassword(PASSWORD);
    req.setNickname("NewUser");
    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/register", req, String.class);
    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    assertEquals(40903, getCode(resp.getBody()));
  }

  @Test
  void testLogin_shouldFailWithInvalidPassword() throws Exception {
    String email = uniqueEmail();
    var sendReq = new SendCodeRequest();
    sendReq.setEmail(email);
    restTemplate.postForEntity("/api/v1/auth/send-code", sendReq, String.class);

    VerificationCode vc = verificationCodeRepository
        .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, "REGISTER")
        .orElseThrow(() -> new AssertionError("Verification code not found"));

    RegisterRequest registerReq = new RegisterRequest();
    registerReq.setEmail(email);
    registerReq.setCode(vc.getCode());
    registerReq.setPassword(PASSWORD);
    registerReq.setNickname("LoginTest");
    restTemplate.postForEntity("/api/v1/auth/register", registerReq, String.class);

    LoginRequest loginReq = new LoginRequest();
    loginReq.setEmail(email);
    loginReq.setPassword("wrong-password");
    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/login", loginReq, String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    assertEquals(40101, getCode(resp.getBody()));
  }

  @Test
  void testMe_shouldFailWithoutToken() {
    ResponseEntity<String> resp = restTemplate.getForEntity(
        "/api/v1/auth/me", String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    JsonNode body = assertDoesNotThrow(() -> objectMapper.readTree(resp.getBody()));
    assertEquals(40100, body.path("code").asInt());
  }
}
