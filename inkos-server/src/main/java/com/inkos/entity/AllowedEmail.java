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
