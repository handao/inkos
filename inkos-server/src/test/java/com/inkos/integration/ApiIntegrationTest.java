package com.inkos.integration;

import com.inkos.dto.request.*;
import com.inkos.entity.VerificationCode;
import com.inkos.repository.VerificationCodeRepository;
import com.inkos.service.EmailService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.MethodName.class)
class ApiIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private VerificationCodeRepository verificationCodeRepository;

  @MockBean
  private EmailService emailService;

  private static final String PASSWORD = "Password123";
  private static final String EMAIL = "api-test-" + System.currentTimeMillis() + "@example.com";
  private static final String NICKNAME = "ApiTestUser";

  private static String accessToken;
  private static String refreshToken;
  private static String bookId;
  private static Long chapterId;

  @BeforeEach
  void setUp() {
    doNothing().when(emailService).sendVerificationCode(anyString(), anyString());
  }

  private HttpEntity<?> authRequest(Object body) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (accessToken != null) {
      headers.setBearerAuth(accessToken);
    }
    return new HttpEntity<>(body, headers);
  }

  private HttpEntity<?> bareRequest(Object body) {
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(body, headers);
  }

  private ResponseEntity<String> get(String path) {
    return restTemplate.exchange(path, HttpMethod.GET, authRequest(null), String.class);
  }

  private ResponseEntity<String> post(String path, Object body) {
    return restTemplate.exchange(path, HttpMethod.POST, authRequest(body), String.class);
  }

  private ResponseEntity<String> put(String path, Object body) {
    return restTemplate.exchange(path, HttpMethod.PUT, authRequest(body), String.class);
  }

  private ResponseEntity<String> delete(String path) {
    return restTemplate.exchange(path, HttpMethod.DELETE, authRequest(null), String.class);
  }

  private int getCode(ResponseEntity<String> resp) throws Exception {
    return objectMapper.readTree(resp.getBody()).get("code").asInt();
  }

  @Test
  void test01_sendCode() throws Exception {
    var req = new SendCodeRequest();
    req.setEmail(EMAIL);
    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/send-code", req, String.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(200, getCode(resp));
  }

  @Test
  void test02_register() throws Exception {
    VerificationCode vc = verificationCodeRepository
        .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(EMAIL, "REGISTER")
        .orElseThrow(() -> new AssertionError("Verification code not found in DB"));

    var req = new RegisterRequest();
    req.setEmail(EMAIL);
    req.setCode(vc.getCode());
    req.setPassword(PASSWORD);
    req.setNickname(NICKNAME);
    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/register", req, String.class);
    assertEquals(HttpStatus.CREATED, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    accessToken = data.path("accessToken").asText();
    refreshToken = data.path("refreshToken").asText();
    assertFalse(accessToken.isEmpty(), "accessToken should not be empty");
    assertFalse(refreshToken.isEmpty(), "refreshToken should not be empty");
    assertEquals(NICKNAME, data.path("user").path("nickname").asText());
    assertEquals(EMAIL, data.path("user").path("email").asText());
    assertEquals("user", data.path("user").path("role").asText());
  }

  @Test
  void test03_login() throws Exception {
    var req = new LoginRequest();
    req.setEmail(EMAIL);
    req.setPassword(PASSWORD);
    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/login", req, String.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    accessToken = data.path("accessToken").asText();
    refreshToken = data.path("refreshToken").asText();
    assertFalse(accessToken.isEmpty(), "accessToken should not be empty");
    assertFalse(refreshToken.isEmpty(), "refreshToken should not be empty");
  }

  @Test
  void test04_getMe() throws Exception {
    ResponseEntity<String> resp = get("/api/v1/auth/me");
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    assertEquals(EMAIL, data.path("email").asText());
    assertEquals(NICKNAME, data.path("nickname").asText());
    assertNotNull(data.path("id").asLong());
    assertEquals("user", data.path("role").asText());
  }

  @Test
  void test05_refreshToken() throws Exception {
    var req = new RefreshTokenRequest();
    req.setRefreshToken(refreshToken);
    ResponseEntity<String> resp = restTemplate.postForEntity(
        "/api/v1/auth/refresh", req, String.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    String newAccessToken = data.path("accessToken").asText();
    assertFalse(newAccessToken.isEmpty(), "New accessToken should not be empty");
    // Verify the new token works
    accessToken = newAccessToken;
    ResponseEntity<String> meResp = get("/api/v1/auth/me");
    assertEquals(HttpStatus.OK, meResp.getStatusCode());
  }

  @Test
  void test06_createBook() throws Exception {
    var req = new CreateBookRequest();
    req.setTitle("测试书籍");
    req.setGenre("fantasy");
    req.setLanguage("zh");
    ResponseEntity<String> resp = post("/api/v1/books", req);
    assertEquals(HttpStatus.CREATED, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    bookId = data.path("id").asText();
    assertNotNull(bookId);
    assertFalse(bookId.isEmpty());
    assertEquals("测试书籍", data.path("title").asText());
    assertEquals("fantasy", data.path("genre").asText());
    assertEquals("zh", data.path("language").asText());
    assertEquals("draft", data.path("status").asText());
  }

  @Test
  void test07_listBooks() throws Exception {
    ResponseEntity<String> resp = get("/api/v1/books");
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode body = objectMapper.readTree(resp.getBody());
    assertEquals(200, body.path("code").asInt());
    JsonNode content = body.path("data").path("content");
    assertTrue(content.isArray(), "books should be a list");
    boolean found = false;
    for (JsonNode book : content) {
      if (bookId.equals(book.path("id").asText())) {
        found = true;
        assertEquals("测试书籍", book.path("title").asText());
        break;
      }
    }
    assertTrue(found, "Created book should appear in book list");
  }

  @Test
  void test08_getBook() throws Exception {
    assertNotNull(bookId, "bookId must be set from test06_createBook");
    ResponseEntity<String> resp = get("/api/v1/books/" + bookId);
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    assertEquals(bookId, data.path("id").asText());
    assertEquals("测试书籍", data.path("title").asText());
    assertEquals("fantasy", data.path("genre").asText());
    assertEquals("zh", data.path("language").asText());
    assertEquals("draft", data.path("status").asText());
    assertTrue(data.path("chapters").isArray(), "chapters should be an array");
    assertEquals(0, data.path("chapters").size());
  }

  @Test
  void test09_updateBook() throws Exception {
    assertNotNull(bookId);
    var req = new UpdateBookRequest();
    req.setTitle("Updated Title");
    req.setOutline("This is a new outline");
    ResponseEntity<String> resp = put("/api/v1/books/" + bookId, req);
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    assertEquals("Updated Title", data.path("title").asText());

    // Verify persistence
    ResponseEntity<String> getResp = get("/api/v1/books/" + bookId);
    JsonNode data2 = objectMapper.readTree(getResp.getBody()).path("data");
    assertEquals("Updated Title", data2.path("title").asText());
    assertEquals("This is a new outline", data2.path("outline").asText());
  }

  @Test
  void test10_createChapter() throws Exception {
    assertNotNull(bookId);
    var req = new CreateChapterRequest();
    req.setChapterNumber(1);
    req.setTitle("第一章");
    req.setContent("这是第一章的内容。");
    ResponseEntity<String> resp = post("/api/v1/books/" + bookId + "/chapters", req);
    assertEquals(HttpStatus.CREATED, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    chapterId = data.path("id").asLong();
    assertTrue(chapterId > 0);
    assertEquals(1, data.path("chapterNumber").asInt());
    assertEquals("第一章", data.path("title").asText());
    assertEquals("这是第一章的内容。", data.path("content").asText());
    assertTrue(data.path("wordCount").asInt() > 0);
    assertEquals("draft", data.path("status").asText());
  }

  @Test
  void test11_listChapters() throws Exception {
    assertNotNull(bookId);
    ResponseEntity<String> resp = get("/api/v1/books/" + bookId + "/chapters");
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    assertTrue(data.isArray());
    assertEquals(1, data.size());
    assertEquals(chapterId, data.get(0).path("id").asLong());
    assertEquals(1, data.get(0).path("chapterNumber").asInt());
  }

  @Test
  void test12_getChapter() throws Exception {
    assertNotNull(chapterId);
    ResponseEntity<String> resp = get("/api/v1/books/" + bookId + "/chapters/" + chapterId);
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    assertEquals(chapterId, data.path("id").asLong());
    assertEquals("第一章", data.path("title").asText());
    assertEquals("这是第一章的内容。", data.path("content").asText());
  }

  @Test
  void test13_updateChapter() throws Exception {
    assertNotNull(chapterId);
    var req = new CreateChapterRequest();
    req.setChapterNumber(1);
    req.setTitle("Updated Chapter");
    req.setContent("Updated content for chapter one.");
    ResponseEntity<String> resp = put("/api/v1/books/" + bookId + "/chapters/" + chapterId, req);
    assertEquals(HttpStatus.OK, resp.getStatusCode());

    JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
    assertEquals("Updated Chapter", data.path("title").asText());
    assertEquals("Updated content for chapter one.", data.path("content").asText());
  }

  @Test
  void test14_deleteChapter() throws Exception {
    assertNotNull(chapterId);
    ResponseEntity<String> resp = delete("/api/v1/books/" + bookId + "/chapters/" + chapterId);
    assertTrue(resp.getStatusCode().is2xxSuccessful());

    // Verify chapter is deleted
    ResponseEntity<String> getResp = get("/api/v1/books/" + bookId + "/chapters/" + chapterId);
    assertEquals(HttpStatus.NOT_FOUND, getResp.getStatusCode());
  }

  @Test
  void test15_deleteBook() throws Exception {
    assertNotNull(bookId);
    ResponseEntity<String> resp = delete("/api/v1/books/" + bookId);
    assertTrue(resp.getStatusCode().is2xxSuccessful());

    // Verify book is deleted
    ResponseEntity<String> getResp = get("/api/v1/books/" + bookId);
    assertEquals(HttpStatus.NOT_FOUND, getResp.getStatusCode());
  }

  @Test
  void test16_adminEndpoints_returns403() throws Exception {
    ResponseEntity<String> resp = get("/api/v1/admin/users");
    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());

    JsonNode body = objectMapper.readTree(resp.getBody());
    assertEquals(40300, body.path("code").asInt());
    assertEquals("权限不足", body.path("message").asText());
  }

  @Test
  void test17_unauthenticatedAccess_returns401() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> resp = restTemplate.exchange(
        "/api/v1/auth/me", HttpMethod.GET,
        new HttpEntity<>(null, headers), String.class);
    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());

    JsonNode body = objectMapper.readTree(resp.getBody());
    assertEquals(40100, body.path("code").asInt());
  }

  @Test
  void test18_createAndGetSession() throws Exception {
    assertNotNull(bookId);
    // Re-create book for session test since it was deleted
    var bookReq = new CreateBookRequest();
    bookReq.setTitle("Session Test Book");
    bookReq.setGenre("scifi");
    ResponseEntity<String> bookResp = post("/api/v1/books", bookReq);
    bookId = objectMapper.readTree(bookResp.getBody()).path("data").path("id").asText();

    var req = new CreateSessionRequest();
    req.setBookId(bookId);
    req.setTitle("写作会话");
    req.setMode("write");
    ResponseEntity<String> createResp = post("/api/v1/sessions", req);
    assertEquals(HttpStatus.CREATED, createResp.getStatusCode());

    JsonNode data = objectMapper.readTree(createResp.getBody()).path("data");
    String sessionId = data.path("sessionId").asText();
    assertNotNull(sessionId);
    assertFalse(sessionId.isEmpty());
    assertEquals("写作会话", data.path("title").asText());
    assertEquals("write", data.path("mode").asText());
    assertTrue(data.path("draft").asBoolean(), "new session should be draft");

    // Get session detail
    ResponseEntity<String> getResp = get("/api/v1/sessions/" + sessionId);
    assertEquals(HttpStatus.OK, getResp.getStatusCode());

    JsonNode detail = objectMapper.readTree(getResp.getBody()).path("data");
    assertEquals(sessionId, detail.path("sessionId").asText());
    assertTrue(detail.path("messages").isArray());

    // List sessions
    ResponseEntity<String> listResp = get("/api/v1/sessions?bookId=" + bookId);
    assertEquals(HttpStatus.OK, listResp.getStatusCode());
    JsonNode list = objectMapper.readTree(listResp.getBody()).path("data");
    assertTrue(list.isArray());
    assertTrue(list.size() >= 1);
  }

  @Test
  void test19_saveAndDeleteLlmService() throws Exception {
    // List services
    ResponseEntity<String> listResp = get("/api/v1/llm/services");
    assertEquals(HttpStatus.OK, listResp.getStatusCode());
    JsonNode listData = objectMapper.readTree(listResp.getBody()).path("data");
    assertTrue(listData.isArray());

    // Save a service
    var req = new SaveLlmServiceRequest();
    req.setServiceType("openai");
    req.setLabel("Test OpenAI");
    req.setBaseUrl("https://api.openai.com/v1");
    req.setApiType("openai");
    req.setDefaultModel("gpt-4");
    ResponseEntity<String> createResp = post("/api/v1/llm/services", req);
    assertEquals(HttpStatus.CREATED, createResp.getStatusCode());

    JsonNode data = objectMapper.readTree(createResp.getBody()).path("data");
    Long serviceId = data.path("id").asLong();
    assertTrue(serviceId > 0);
    assertEquals("openai", data.path("serviceType").asText());
    assertEquals("Test OpenAI", data.path("label").asText());

    // Save a secret for the service
    var secretReq = new SaveSecretsRequest();
    secretReq.setApiKey("sk-test-key-12345");
    ResponseEntity<String> secretResp = put("/api/v1/llm/secrets/openai", secretReq);
    assertEquals(HttpStatus.OK, secretResp.getStatusCode());

    // Delete the service
    ResponseEntity<String> deleteResp = delete("/api/v1/llm/services/" + serviceId);
    assertTrue(deleteResp.getStatusCode().is2xxSuccessful());
  }

  @Test
  void test20_adminEndpoints_requireAdminRole() throws Exception {
    // Verify all admin endpoints return 403 for non-admin user
    String[] adminPaths = {
        "/api/v1/admin/users",
        "/api/v1/admin/users/1/status",
        "/api/v1/admin/allowed-emails",
        "/api/v1/admin/allowed-emails/1"
    };

    for (String path : adminPaths) {
      HttpMethod method = path.contains("status") ? HttpMethod.PUT : HttpMethod.GET;
      ResponseEntity<String> resp = restTemplate.exchange(
          path, method, authRequest(null), String.class);
      assertTrue(resp.getStatusCode() == HttpStatus.FORBIDDEN
              || resp.getStatusCode() == HttpStatus.NOT_FOUND,
          "Expected 403/404 for " + path + " but got " + resp.getStatusCode());
    }
  }
}
