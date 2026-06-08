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
    private long accessTokenExpiration = 900_000;
    private long refreshTokenExpiration = 604_800_000;
}
