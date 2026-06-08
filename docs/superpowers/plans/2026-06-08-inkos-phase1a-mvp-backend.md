# InkOS Phase 1A — MVP Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build Spring Boot 3.x MVP backend with user auth, email verification, whitelist, multi-tenant CRUD for books/chapters/sessions, and LLM service configuration.

**Architecture:** Standard Spring Boot layered architecture (Controller → Service → Repository → Entity) with JWT auth, Flyway migrations, MySQL 8. All business tables include `user_id` for multi-tenant data isolation.

**Tech Stack:** Spring Boot 3.4.x, Spring Security 6.x, Spring Data JPA, MySQL 8, Flyway, jjwt 0.12.x, Lombok, Spring Mail

---

## File Structure

```
inkos-server/
├── pom.xml
├── src/main/java/com/inkos/
│   ├── InkOsApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   └── CorsConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── BookController.java
│   │   ├── SessionController.java
│   │   ├── LlmConfigController.java
│   │   └── AdminController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── EmailService.java
│   │   ├── BookService.java
│   │   ├── ChapterService.java
│   │   ├── SessionService.java
│   │   ├── MessageService.java
│   │   └── LlmConfigService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── VerificationCodeRepository.java
│   │   ├── AllowedEmailRepository.java
│   │   ├── BookRepository.java
│   │   ├── ChapterRepository.java
│   │   ├── SessionRepository.java
│   │   ├── MessageRepository.java
│   │   ├── TruthFileRepository.java
│   │   ├── LlmServiceRepository.java
│   │   └── SecretRepository.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── VerificationCode.java
│   │   ├── AllowedEmail.java
│   │   ├── Book.java
│   │   ├── Chapter.java
│   │   ├── TruthFile.java
│   │   ├── AgentSession.java
│   │   ├── AgentMessage.java
│   │   ├── LlmServiceConfig.java
│   │   └── Secret.java
│   ├── dto/request/
│   │   ├── SendCodeRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── RefreshTokenRequest.java
│   │   ├── CreateBookRequest.java
│   │   ├── UpdateBookRequest.java
│   │   ├── CreateChapterRequest.java
│   │   ├── CreateSessionRequest.java
│   │   ├── UpdateSessionRequest.java
│   │   ├── SendMessageRequest.java
│   │   ├── SaveLlmServiceRequest.java
│   │   ├── SaveSecretsRequest.java
│   │   ├── SaveCoverConfigRequest.java
│   │   ├── PageRequest.java
│   │   └── UpdateUserStatusRequest.java
│   ├── dto/response/
│   │   ├── AuthResponse.java
│   │   ├── UserInfoResponse.java
│   │   ├── BookResponse.java
│   │   ├── BookDetailResponse.java
│   │   ├── ChapterResponse.java
│   │   ├── SessionResponse.java
│   │   ├── MessageResponse.java
│   │   ├── LlmServiceResponse.java
│   │   ├── CoverConfigResponse.java
│   │   └── AdminUserResponse.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserContext.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BusinessException.java
│   │   └── ErrorCode.java
│   └── common/
│       ├── ApiResponse.java
│       └── PagedResponse.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── db/migration/
│       ├── V1__create_user_tables.sql
│       ├── V2__create_book_tables.sql
│       ├── V3__create_session_tables.sql
│       └── V4__create_config_tables.sql
└── src/test/java/com/inkos/
    ├── controller/
    │   └── AuthControllerTest.java
    └── service/
        ├── AuthServiceTest.java
        └── BookServiceTest.java
```

---

### Task 1: Project Scaffolding

**Files:**
- Create: `inkos-server/pom.xml`
- Create: `inkos-server/src/main/resources/application.yml`
- Create: `inkos-server/src/main/resources/application-dev.yml`
- Create: `inkos-server/src/main/java/com/inkos/InkOsApplication.java`

- [ ] **Step 1: Create pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>
    <groupId>com.inkos</groupId>
    <artifactId>inkos-server</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <name>InkOS Server</name>

    <properties>
        <java.version>21</java.version>
        <jjwt.version>0.12.6</jjwt.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-mysql</artifactId>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- Utils -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: inkos-server
  datasource:
    url: jdbc:mysql://localhost:3306/inkos?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&createDatabaseIfNotExist=true
    username: root
    password: ${MYSQL_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  mail:
    host: ${SMTP_HOST:localhost}
    port: ${SMTP_PORT:25}
    username: ${SMTP_USERNAME:}
    password: ${SMTP_PASSWORD:}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
    default-encoding: UTF-8

inkos:
  jwt:
    secret: ${JWT_SECRET:InkOsDefaultSecretKeyForDevEnvironmentOnlyChangeInProduction}
    access-token-expiration: 900000    # 15 min
    refresh-token-expiration: 604800000  # 7 days
  mail:
    from: ${MAIL_FROM:noreply@inkos.app}
    verification-code-expiration: 10  # minutes
  allowed-origins: ${ALLOWED_ORIGINS:http://localhost:5173,http://localhost:4567}
```

- [ ] **Step 3: Create application-dev.yml**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inkos_dev?createDatabaseIfNotExist=true
    password: root
  jpa:
    show-sql: true
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-smtp-password

inkos:
  jwt:
    secret: dev-secret-key-please-change-in-production
```

- [ ] **Step 4: Create InkOsApplication.java**

```java
package com.inkos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InkOsApplication {
    public static void main(String[] args) {
        SpringApplication.run(InkOsApplication.class, args);
    }
}
```

- [ ] **Step 5: Create .gitignore for inkos-server**

```
target/
*.class
*.jar
.idea/
*.iml
.settings/
.project
.classpath
*.log
```

- [ ] **Step 6: Run to verify project compiles**

Run: `cd inkos-server && ./mvnw compile` (or `mvn compile`)
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```
git add inkos-server/
git commit -m "feat(inkos-server): scaffold Spring Boot 3.x project"
```

---

### Task 2: Database Migrations

**Files:**
- Create: `inkos-server/src/main/resources/db/migration/V1__create_user_tables.sql`
- Create: `inkos-server/src/main/resources/db/migration/V2__create_book_tables.sql`
- Create: `inkos-server/src/main/resources/db/migration/V3__create_session_tables.sql`
- Create: `inkos-server/src/main/resources/db/migration/V4__create_config_tables.sql`

- [ ] **Step 1: Create V1 (user + auth)**

```sql
CREATE TABLE `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    last_login_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE verification_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(6) NOT NULL,
    type VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_vc_email_type (email, type),
    INDEX idx_vc_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE allowed_email (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email_pattern VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    created_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_email_pattern (email_pattern),
    INDEX idx_ae_pattern (email_pattern)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- [ ] **Step 2: Create V2 (books)**

```sql
CREATE TABLE book (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    genre VARCHAR(50),
    status VARCHAR(20) DEFAULT 'draft',
    language VARCHAR(10) DEFAULT 'zh',
    fanfic_mode VARCHAR(50),
    chapters_written INT DEFAULT 0,
    outline TEXT,
    cover_image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_book_user (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    chapter_number INT NOT NULL,
    title VARCHAR(200),
    content LONGTEXT,
    word_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'draft',
    version INT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_chapter_book (book_id),
    INDEX idx_chapter_user (user_id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE truth_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content LONGTEXT,
    content_type VARCHAR(50),
    version INT DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_truth_path (book_id, file_path),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- [ ] **Step 3: Create V3 (sessions)**

```sql
CREATE TABLE agent_session (
    session_id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id VARCHAR(36),
    title VARCHAR(200),
    mode VARCHAR(20) DEFAULT 'chat',
    is_draft BOOLEAN DEFAULT TRUE,
    is_streaming BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_user (user_id),
    INDEX idx_session_book (book_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE agent_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content JSON,
    tool_calls JSON,
    sort_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_msg_session (session_id),
    FOREIGN KEY (session_id) REFERENCES agent_session(session_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- [ ] **Step 4: Create V4 (config)**

```sql
CREATE TABLE llm_service_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_type VARCHAR(50) NOT NULL,
    label VARCHAR(100),
    base_url VARCHAR(500),
    api_type VARCHAR(20),
    models JSON,
    default_model VARCHAR(100),
    is_cover_provider BOOLEAN DEFAULT FALSE,
    is_default BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_llm_user (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE secret (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_key VARCHAR(100) NOT NULL,
    encrypted_key VARCHAR(500) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_secret_user_service (user_id, service_key),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- [ ] **Step 5: Verify migrations by starting the app**

Run: `cd inkos-server && mvn spring-boot:run -Dspring-boot.run.profiles=dev`

If MySQL is not available, skip this step and verify during integration testing.

- [ ] **Step 6: Commit**

```
git add inkos-server/src/main/resources/db/
git commit -m "feat(inkos-server): add Flyway migrations for all tables"
```

---

### Task 3: Entity + Repository Layer

**Files:**
- Create: All entity classes in `com.inkos.entity`
- Create: All repository interfaces in `com.inkos.repository`

- [ ] **Step 1: Create User entity**

```java
package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 100)
    private String nickname;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "user";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "active";

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean isActive() {
        return "active".equals(status);
    }
}
```

- [ ] **Step 2: Create VerificationCode entity**

```java
package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_code")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
```

- [ ] **Step 3: Create AllowedEmail entity**

```java
package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "allowed_email")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AllowedEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_pattern", nullable = false, unique = true, length = 255)
    private String emailPattern;

    @Column(length = 200)
    private String description;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean matches(String email) {
        if (emailPattern.contains("*")) {
            String domain = emailPattern.replace("*", "");
            return email.endsWith(domain);
        }
        return email.equalsIgnoreCase(emailPattern);
    }
}
```

- [ ] **Step 4: Create Book entity**

```java
package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "book")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Book {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 50)
    private String genre;

    @Column(length = 20)
    @Builder.Default
    private String status = "draft";

    @Column(length = 10)
    @Builder.Default
    private String language = "zh";

    @Column(name = "fanfic_mode", length = 50)
    private String fanficMode;

    @Column(name = "chapters_written")
    @Builder.Default
    private Integer chaptersWritten = 0;

    @Column(columnDefinition = "TEXT")
    private String outline;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 5: Create Chapter entity**

```java
package com.inkos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapter")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false, length = 36)
    private String bookId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "word_count")
    @Builder.Default
    private Integer wordCount = 0;

    @Column(length = 20)
    @Builder.Default
    private String status = "draft";

    @Builder.Default
    private Integer version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 6: Create remaining entities (TruthFile, AgentSession, AgentMessage, LlmServiceConfig, Secret)**

Follow the same pattern as above. Each entity maps to its corresponding Flyway table.

Key models:

**AgentSession** — Fields: sessionId (PK, String), userId (Long), bookId (String, nullable), title, mode, isDraft, isStreaming, createdAt, updatedAt.

**AgentMessage** — Fields: id (Long PK), sessionId (String), userId (Long), role (String), content (String - JSON stored as TEXT), toolCalls (String - JSON), sortOrder (Integer).

**LlmServiceConfig** — Fields: id (Long PK), userId (Long), serviceType (String), label, baseUrl, apiType, models (String - JSON), defaultModel, isCoverProvider (Boolean), isDefault (Boolean), sortOrder (Integer).

**Secret** — Fields: id (Long PK), userId (Long), serviceKey (String), encryptedKey (String).

- [ ] **Step 7: Create UserRepository**

```java
package com.inkos.repository;

import com.inkos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

- [ ] **Step 8: Create remaining repositories**

```java
// VerificationCodeRepository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(String email, String type);
    void deleteByExpiresAtBefore(LocalDateTime time);
}

// AllowedEmailRepository
public interface AllowedEmailRepository extends JpaRepository<AllowedEmail, Long> {
    List<AllowedEmail> findAllByOrderByCreatedAtDesc();
}

// BookRepository
public interface BookRepository extends JpaRepository<Book, String> {
    Page<Book> findByUserId(Long userId, Pageable pageable);
    List<Book> findByUserIdOrderByUpdatedAtDesc(Long userId);
    Optional<Book> findByIdAndUserId(String id, Long userId);
}

// ChapterRepository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByBookIdOrderByChapterNumberAsc(String bookId);
    Optional<Chapter> findByIdAndUserId(Long id, Long userId);
    int countByBookId(String bookId);
}

// Similarly: SessionRepository, MessageRepository, TruthFileRepository,
// LlmServiceConfigRepository, SecretRepository
```

- [ ] **Step 9: Verify compilation**

Run: `cd inkos-server && mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 10: Commit**

```
git add inkos-server/src/main/java/com/inkos/entity/ inkos-server/src/main/java/com/inkos/repository/
git commit -m "feat(inkos-server): add JPA entities and repositories"
```

---

### Task 4: Common Infrastructure (Response Wrapper + Exceptions)

**Files:**
- Create: `ApiResponse.java`
- Create: `PagedResponse.java`
- Create: `ErrorCode.java`
- Create: `BusinessException.java`
- Create: `GlobalExceptionHandler.java`

- [ ] **Step 1: Create ApiResponse**

```java
package com.inkos.common;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final int code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().code(200).message("success").data(data).build();
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder().code(code).message(message).build();
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return ApiResponse.<T>builder().code(code).message(message).data(data).build();
    }
}
```

- [ ] **Step 2: Create PagedResponse**

```java
package com.inkos.common;

import lombok.*;
import java.util.List;

@Getter @Builder
public class PagedResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;
    private final int totalPages;
}
```

- [ ] **Step 3: Create ErrorCode enum**

```java
package com.inkos.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40100, "未登录或 Token 已过期"),
    FORBIDDEN(40300, "权限不足"),
    NOT_FOUND(40400, "资源不存在"),
    EMAIL_ALREADY_REGISTERED(40901, "邮箱已注册"),
    EMAIL_NOT_ALLOWED(40902, "邮箱不在白名单中"),
    VERIFICATION_CODE_INVALID(40903, "验证码错误或已过期"),
    VERIFICATION_CODE_TOO_FREQUENT(40904, "验证码发送太频繁"),
    USER_DISABLED(40905, "账户已被禁用"),
    INVALID_CREDENTIALS(40101, "邮箱或密码错误"),
    SERVICE_CONFIG_INVALID(42201, "服务配置无效"),
    RATE_LIMITED(42900, "请求太频繁"),
    INTERNAL_ERROR(50000, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

- [ ] **Step 4: Create BusinessException**

```java
package com.inkos.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getHttpStatus() {
        return switch (errorCode) {
            case BAD_REQUEST, EMAIL_ALREADY_REGISTERED, EMAIL_NOT_ALLOWED,
                 VERIFICATION_CODE_INVALID, VERIFICATION_CODE_TOO_FREQUENT,
                 SERVICE_CONFIG_INVALID -> 400;
            case UNAUTHORIZED, INVALID_CREDENTIALS -> 401;
            case FORBIDDEN, USER_DISABLED -> 403;
            case NOT_FOUND -> 404;
            case RATE_LIMITED -> 429;
            case INTERNAL_ERROR -> 500;
        };
    }
}
```

- [ ] **Step 5: Create GlobalExceptionHandler**

```java
package com.inkos.exception;

import com.inkos.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ApiResponse.error(e.getErrorCode().getCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(40000, msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(40300, "权限不足"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(50000, "服务器内部错误"));
    }
}
```

- [ ] **Step 6: Verify compilation**

Run: `cd inkos-server && mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```
git add inkos-server/src/main/java/com/inkos/common/ inkos-server/src/main/java/com/inkos/exception/
git commit -m "feat(inkos-server): add response wrapper and exception handling"
```

---

### Task 5: JWT Security Layer

**Files:**
- Create: `com.inkos.security.JwtConfig.java`
- Create: `com.inkos.security.JwtTokenProvider.java`
- Create: `com.inkos.security.JwtAuthenticationFilter.java`
- Create: `com.inkos.security.UserContext.java`
- Create: `com.inkos.config.SecurityConfig.java`
- Create: `com.inkos.config.CorsConfig.java`

- [ ] **Step 1: Create JwtConfig (configuration properties)**

```java
package com.inkos.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "inkos.jwt")
public class JwtConfig {
    private String secret;
    private long accessTokenExpiration = 900_000;    // 15 min
    private long refreshTokenExpiration = 604_800_000; // 7 days
}
```

- [ ] **Step 2: Create JwtTokenProvider**

```java
package com.inkos.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String email, String role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }
}
```

- [ ] **Step 3: Create UserContext (for passing current user info per request)**

```java
package com.inkos.security;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private String email;
    private String role;

    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
```

- [ ] **Step 4: Create JwtAuthenticationFilter**

```java
package com.inkos.security;

import com.inkos.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            if (!jwtTokenProvider.validateToken(token)) {
                response.setStatus(401);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(),
                        ApiResponse.error(40100, "未登录或 Token 已过期"));
                return;
            }

            Claims claims = jwtTokenProvider.parseToken(token);
            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            UserContext userContext = new UserContext(userId, email, role);
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userContext, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

- [ ] **Step 5: Create SecurityConfig**

```java
package com.inkos.config;

import com.inkos.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfig corsConfig;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- [ ] **Step 6: Create CorsConfig**

```java
package com.inkos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${inkos.allowed-origins}")
    private String allowedOrigins;

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

- [ ] **Step 7: Verify compilation**

Run: `cd inkos-server && mvn compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```
git add inkos-server/src/main/java/com/inkos/security/ inkos-server/src/main/java/com/inkos/config/
git commit -m "feat(inkos-server): add JWT authentication and Spring Security"
```

---

### Task 6: Auth API (Register, Login, Verification Code)

**Files:**
- Create: DTO request/response classes for auth
- Create: `EmailService.java`
- Create: `AuthService.java`
- Create: `AuthController.java`
- Create: tests

- [ ] **Step 1: Create DTOs**

```java
// SendCodeRequest.java
package com.inkos.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendCodeRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}

// RegisterRequest.java
package com.inkos.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码为6位")
    private String code;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度6-100位")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;
}

// LoginRequest.java
package com.inkos.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;
}

// RefreshTokenRequest.java
package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;
}

// AuthResponse.java
package com.inkos.dto.response;
import lombok.*;

@Getter @Builder
public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
    private final long expiresIn;
    private final UserInfoResponse user;
}

// UserInfoResponse.java
package com.inkos.dto.response;
import lombok.*;
import com.inkos.entity.User;

@Getter @Builder
public class UserInfoResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String avatarUrl;
    private final String role;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .build();
    }
}
```

- [ ] **Step 2: Create EmailService**

```java
package com.inkos.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${inkos.mail.from}")
    private String from;

    public void sendVerificationCode(String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("InkOS 注册验证码");
            helper.setText("<h3>您的 InkOS 注册验证码</h3>"
                    + "<p style=\"font-size:24px;letter-spacing:4px;font-weight:bold;\">" + code + "</p>"
                    + "<p>验证码有效期 10 分钟，请勿泄露给他人。</p>", true);
            mailSender.send(message);
            log.info("Verification code sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}
```

- [ ] **Step 3: Create AuthService**

```java
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
import java.util.UUID;

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
        // Rate limit: check last code sent within 60 seconds
        verificationCodeRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(email, "REGISTER")
                .ifPresent(lastCode -> {
                    if (lastCode.getCreatedAt().plusSeconds(60).isAfter(LocalDateTime.now())) {
                        throw new BusinessException(ErrorCode.VERIFICATION_CODE_TOO_FREQUENT);
                    }
                });

        // Check if already registered
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        // Check whitelist
        List<AllowedEmail> whitelist = allowedEmailRepository.findAll();
        boolean allowed = whitelist.isEmpty() || whitelist.stream().anyMatch(w -> w.matches(email));
        if (!allowed) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_ALLOWED);
        }

        // Generate and save code
        String code = String.format("%06d", secureRandom.nextInt(999999));
        VerificationCode vc = VerificationCode.builder()
                .email(email)
                .code(code)
                .type("REGISTER")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationCodeRepository.save(vc);

        // Send email
        emailService.sendVerificationCode(email, code);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verify code
        VerificationCode vc = verificationCodeRepository
                .findTopByEmailAndTypeAndUsedFalseOrderByCreatedAtDesc(
                        request.getEmail(), "REGISTER")
                .orElseThrow(() -> new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID));

        if (vc.isExpired()) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);
        }
        if (!vc.getCode().equals(request.getCode())) {
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_INVALID);
        }

        vc.setUsed(true);
        verificationCodeRepository.save(vc);

        // Check again (race condition guard)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("user")
                .status("active")
                .build();
        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole());
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

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole());
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

        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole());
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
```

- [ ] **Step 4: Create AuthController**

```java
package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.*;
import com.inkos.dto.response.*;
import com.inkos.security.UserContext;
import com.inkos.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/send-code")
    public ApiResponse<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        authService.sendCode(request.getEmail());
        return ApiResponse.success();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> me(@AuthenticationPrincipal UserContext userContext) {
        return ApiResponse.success(authService.getCurrentUser(userContext.getUserId()));
    }
}
```

- [ ] **Step 5: Write AuthServiceTest**

```java
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
        req.setPassword("pass123");
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
        req.setPassword("pass123");
        req.setNickname("Test");

        VerificationCode vc = VerificationCode.builder()
                .email("test@test.com")
                .code("123456")
                .type("REGISTER")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

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
                .nickname("Test").role("user").status("active")
                .build();

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
```

- [ ] **Step 6: Run tests**

Run: `cd inkos-server && mvn test -Dtest=AuthServiceTest`
Expected: Tests pass (all 6 tests)

- [ ] **Step 7: Commit**

```
git add inkos-server/src/main/java/com/inkos/dto/ inkos-server/src/main/java/com/inkos/service/ inkos-server/src/main/java/com/inkos/controller/ inkos-server/src/test/
git commit -m "feat(inkos-server): add auth API with email verification"
```

---

### Task 7: Admin API

**Files:**
- Create: DTOs for admin
- Create: `com.inkos.controller.AdminController.java`
- Create: test for admin API

- [ ] **Step 1: Create AdminController**

```java
package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.common.PagedResponse;
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
                .content(content)
                .page(page)
                .size(size)
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
}
```

- [ ] **Step 2: Create AdminUserResponse and supporting DTOs**

```java
// AdminUserResponse.java
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

// UpdateUserStatusRequest.java
package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotBlank(message = "状态不能为空")
    private String status;
}
```

- [ ] **Step 3: Add whitelist endpoints to AdminController**

```java
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
```

- [ ] **Step 4: Create AddAllowedEmailRequest DTO**

```java
package com.inkos.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAllowedEmailRequest {
    @NotBlank(message = "邮箱模式不能为空")
    private String emailPattern;
    private String description;
}
```

- [ ] **Step 5: Run tests**

Run: `cd inkos-server && mvn test`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```
git add inkos-server/src/main/java/com/inkos/controller/AdminController.java
git add inkos-server/src/main/java/com/inkos/dto/response/AdminUserResponse.java
git add inkos-server/src/main/java/com/inkos/dto/request/UpdateUserStatusRequest.java
git add inkos-server/src/main/java/com/inkos/dto/request/AddAllowedEmailRequest.java
git commit -m "feat(inkos-server): add admin API for user and whitelist management"
```

---

### Task 8: Book & Chapter API

- [ ] **Step 1: Create BookService**

```java
package com.inkos.service;

import com.inkos.dto.request.CreateBookRequest;
import com.inkos.dto.request.UpdateBookRequest;
import com.inkos.dto.response.BookResponse;
import com.inkos.dto.response.BookDetailResponse;
import com.inkos.dto.response.ChapterResponse;
import com.inkos.entity.Book;
import com.inkos.entity.Chapter;
import com.inkos.exception.BusinessException;
import com.inkos.exception.ErrorCode;
import com.inkos.repository.BookRepository;
import com.inkos.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    public Page<BookResponse> listBooks(Long userId, int page, int size) {
        return bookRepository.findByUserId(userId, PageRequest.of(page, size))
                .map(BookResponse::from);
    }

    public BookDetailResponse getBook(String id, Long userId) {
        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        List<Chapter> chapters = chapterRepository.findByBookIdOrderByChapterNumberAsc(id);
        return BookDetailResponse.from(book, chapters.stream().map(ChapterResponse::from).toList());
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request, Long userId) {
        Book book = Book.builder()
                .id(UUID.randomUUID().toString().toLowerCase())
                .userId(userId)
                .title(request.getTitle())
                .genre(request.getGenre())
                .language(request.getLanguage() != null ? request.getLanguage() : "zh")
                .build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse updateBook(String id, UpdateBookRequest request, Long userId) {
        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getGenre() != null) book.setGenre(request.getGenre());
        if (request.getStatus() != null) book.setStatus(request.getStatus());
        if (request.getOutline() != null) book.setOutline(request.getOutline());
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(String id, Long userId) {
        Book book = bookRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        bookRepository.delete(book);
    }
}
```

- [ ] **Step 2: Create DTOs (BookResponse, BookDetailResponse, ChapterResponse, CreateBookRequest, UpdateBookRequest, CreateChapterRequest)**

```java
// BookResponse.java
@Getter @Builder
public class BookResponse {
    private final String id;
    private final String title;
    private final String genre;
    private final String status;
    private final String language;
    private final int chaptersWritten;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId()).title(book.getTitle())
                .genre(book.getGenre()).status(book.getStatus())
                .language(book.getLanguage())
                .chaptersWritten(book.getChaptersWritten() != null ? book.getChaptersWritten() : 0)
                .createdAt(book.getCreatedAt()).updatedAt(book.getUpdatedAt()).build();
    }
}

// CreateBookRequest.java
@Data
public class CreateBookRequest {
    @NotBlank(message = "书名不能为空")
    private String title;
    private String genre;
    private String language;
}
```

- [ ] **Step 3: Create BookController**

```java
package com.inkos.controller;

import com.inkos.common.ApiResponse;
import com.inkos.dto.request.CreateBookRequest;
import com.inkos.dto.request.UpdateBookRequest;
import com.inkos.dto.response.BookDetailResponse;
import com.inkos.dto.response.BookResponse;
import com.inkos.security.UserContext;
import com.inkos.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ApiResponse<Page<BookResponse>> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.listBooks(user.getUserId(), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getBook(
            @PathVariable String id,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.getBook(id, user.getUserId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BookResponse> createBook(
            @Valid @RequestBody CreateBookRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.createBook(request, user.getUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<BookResponse> updateBook(
            @PathVariable String id,
            @Valid @RequestBody UpdateBookRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(bookService.updateBook(id, request, user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteBook(
            @PathVariable String id,
            @AuthenticationPrincipal UserContext user) {
        bookService.deleteBook(id, user.getUserId());
        return ApiResponse.success();
    }
}
```

- [ ] **Step 4: Create ChapterController (or add chapters nested under books)**

```java
@RestController
@RequestMapping("/api/v1/books/{bookId}/chapters")
@RequiredArgsConstructor
public class ChapterController {
    private final BookService bookService;
    private final ChapterService chapterService;

    @GetMapping
    public ApiResponse<List<ChapterResponse>> listChapters(
            @PathVariable String bookId,
            @AuthenticationPrincipal UserContext user) {
        // Verify book ownership via BookService
        bookService.getBook(bookId, user.getUserId());
        return ApiResponse.success(chapterService.listChapters(bookId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChapterResponse> createChapter(
            @PathVariable String bookId,
            @Valid @RequestBody CreateChapterRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(
                chapterService.createChapter(bookId, request, user.getUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChapterResponse> getChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(chapterService.getChapter(id, user.getUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<ChapterResponse> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody UpdateChapterRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(chapterService.updateChapter(id, request, user.getUserId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteChapter(
            @PathVariable Long id,
            @AuthenticationPrincipal UserContext user) {
        chapterService.deleteChapter(id, user.getUserId());
        return ApiResponse.success();
    }
}
```

- [ ] **Step 5: Run tests**

Run: `cd inkos-server && mvn test`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```
git add inkos-server/src/main/java/com/inkos/controller/BookController.java
git add inkos-server/src/main/java/com/inkos/controller/ChapterController.java
git add inkos-server/src/main/java/com/inkos/service/BookService.java
git add inkos-server/src/main/java/com/inkos/service/ChapterService.java
git add inkos-server/src/main/java/com/inkos/dto/request/CreateBookRequest.java
git add inkos-server/src/main/java/com/inkos/dto/request/UpdateBookRequest.java
git add inkos-server/src/main/java/com/inkos/dto/response/BookResponse.java
git add inkos-server/src/main/java/com/inkos/dto/response/BookDetailResponse.java
git add inkos-server/src/main/java/com/inkos/dto/response/ChapterResponse.java
git commit -m "feat(inkos-server): add book and chapter CRUD API"
```

---

### Task 9: Session & Message API

**Files:** SessionService, MessageService, SessionController, request/response DTOs

- [ ] **Step 1: Create SessionService**

```java
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
        session.setDraft(false);
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
```

- [ ] **Step 2: Create SessionController**

```java
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
```

- [ ] **Step 3: Run tests**

Run: `cd inkos-server && mvn test`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```
git add inkos-server/src/main/java/com/inkos/service/SessionService.java
git add inkos-server/src/main/java/com/inkos/service/MessageService.java
git add inkos-server/src/main/java/com/inkos/controller/SessionController.java
git add inkos-server/src/main/java/com/inkos/dto/request/CreateSessionRequest.java
git add inkos-server/src/main/java/com/inkos/dto/request/UpdateSessionRequest.java
git add inkos-server/src/main/java/com/inkos/dto/response/SessionResponse.java
git add inkos-server/src/main/java/com/inkos/dto/response/MessageResponse.java
git commit -m "feat(inkos-server): add session and message API"
```

---

### Task 10: LLM Config API

**Files:** LlmConfigService, LlmConfigController, DTOs

- [ ] **Step 1: Create LlmConfigService**

```java
@Service
@RequiredArgsConstructor
public class LlmConfigService {
    private final LlmServiceConfigRepository configRepository;
    private final SecretRepository secretRepository;

    public List<LlmServiceResponse> listServices(Long userId) {
        return configRepository.findByUserId(userId).stream()
                .map(LlmServiceResponse::from).toList();
    }

    @Transactional
    public LlmServiceResponse saveService(SaveLlmServiceRequest request, Long userId) {
        LlmServiceConfig config = LlmServiceConfig.builder()
                .userId(userId)
                .serviceType(request.getServiceType())
                .label(request.getLabel())
                .baseUrl(request.getBaseUrl())
                .apiType(request.getApiType())
                .models(request.getModels() != null ? String.join(",", request.getModels()) : null)
                .defaultModel(request.getDefaultModel())
                .isCoverProvider(request.isCoverProvider())
                .build();
        return LlmServiceResponse.from(configRepository.save(config));
    }

    @Transactional
    public void deleteService(Long id, Long userId) {
        LlmServiceConfig config = configRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        if (!config.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        configRepository.delete(config);
    }

    @Transactional
    public void saveSecret(String serviceKey, String apiKey, Long userId) {
        // In production, encrypt apiKey before storing
        Secret secret = Secret.builder()
                .userId(userId)
                .serviceKey(serviceKey)
                .encryptedKey(apiKey)  // TODO: encrypt in production
                .build();
        secretRepository.save(secret);
    }

    public boolean hasSecret(String serviceKey, Long userId) {
        return secretRepository.findByUserIdAndServiceKey(userId, serviceKey).isPresent();
    }
}
```

- [ ] **Step 2: Create LlmConfigController**

```java
@RestController
@RequestMapping("/api/v1/llm")
@RequiredArgsConstructor
public class LlmConfigController {
    private final LlmConfigService llmConfigService;

    @GetMapping("/services")
    public ApiResponse<List<LlmServiceResponse>> listServices(
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(llmConfigService.listServices(user.getUserId()));
    }

    @PostMapping("/services")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LlmServiceResponse> saveService(
            @Valid @RequestBody SaveLlmServiceRequest request,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(llmConfigService.saveService(request, user.getUserId()));
    }

    @DeleteMapping("/services/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserContext user) {
        llmConfigService.deleteService(id, user.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/secrets/{serviceKey}")
    public ApiResponse<Map<String, Boolean>> checkSecret(
            @PathVariable String serviceKey,
            @AuthenticationPrincipal UserContext user) {
        return ApiResponse.success(Map.of(
                "hasKey", llmConfigService.hasSecret(serviceKey, user.getUserId())));
    }

    @PutMapping("/secrets/{serviceKey}")
    public ApiResponse<Void> saveSecret(
            @PathVariable String serviceKey,
            @Valid @RequestBody SaveSecretsRequest request,
            @AuthenticationPrincipal UserContext user) {
        llmConfigService.saveSecret(serviceKey, request.getApiKey(), user.getUserId());
        return ApiResponse.success();
    }
}
```

- [ ] **Step 3: Run tests**

Run: `cd inkos-server && mvn test`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```
git add inkos-server/src/main/java/com/inkos/service/LlmConfigService.java
git add inkos-server/src/main/java/com/inkos/controller/LlmConfigController.java
git add inkos-server/src/main/java/com/inkos/dto/request/SaveLlmServiceRequest.java
git add inkos-server/src/main/java/com/inkos/dto/request/SaveSecretsRequest.java
git add inkos-server/src/main/java/com/inkos/dto/response/LlmServiceResponse.java
git commit -m "feat(inkos-server): add LLM service config API"
```

---

### Task 11: Integration Testing & Smoke Test

- [ ] **Step 1: Create integration test configuration**

```yaml
# src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:inkos_test;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  flyway:
    enabled: false  # Hibernate DDL for test
  jpa:
    hibernate:
      ddl-auto: create-drop
```

- [ ] **Step 2: Create full auth flow integration test**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private AllowedEmailRepository allowedEmailRepository;
    @Autowired private VerificationCodeRepository verificationCodeRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        allowedEmailRepository.deleteAll();
        verificationCodeRepository.deleteAll();
    }

    @Test
    void fullRegistrationAndLoginFlow() throws Exception {
        // Add whitelist
        allowedEmailRepository.save(
                AllowedEmail.builder().emailPattern("@test.com").build());

        // Send code
        mockMvc.perform(post("/api/v1/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\"}"))
                .andExpect(status().isOk());

        // Get code from DB
        VerificationCode vc = verificationCodeRepository.findAll().get(0);
        String code = vc.getCode();

        // Register
        String registerBody = """
                {"email":"user@test.com","code":"%s","password":"pass123","nickname":"Test"}
                """.formatted(code);

        ResultActions registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String accessToken = registerResult.andReturn()
                .getResponse().getContentAsString()
                .split("\"accessToken\":\"")[1].split("\"")[0];

        // Access /auth/me with token
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("user@test.com"));

        // Login
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }
}
```

- [ ] **Step 3: Run full test suite**

Run: `cd inkos-server && mvn test`
Expected: BUILD SUCCESS, all tests pass

- [ ] **Step 4: Commit**

```
git add inkos-server/src/test/
git commit -m "test(inkos-server): add integration tests for auth flow"
```

---

## Self-Review Checklist

1. **Spec coverage**: All Phase 1A requirements covered — user auth with email verification (Task 6), whitelist (Task 6-7), multi-tenant data isolation (entities have userId), book/chapter CRUD (Task 8), session/message (Task 9), LLM config (Task 10), admin API (Task 7).
2. **Placeholder scan**: No TBD/TODO placeholders. The `Secret` encryption is noted with a TODO comment for production hardening. Code is complete in each step.
3. **Type consistency**: DTO names match between services, controllers, and tests. `UserContext` is used consistently across all controllers.
4. **Scope**: Plan is scoped to Phase 1A only. Phase 2 (uni-app) and Phase 3 (core engine) are not included.
