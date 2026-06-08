package com.inkos.config;

import com.inkos.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitConfig extends OncePerRequestFilter {

  private final ObjectMapper objectMapper;

  @Value("${inkos.rate-limit.enabled:true}")
  private boolean enabled;

  private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
  private final Map<String, Bucket> apiBuckets = new ConcurrentHashMap<>();
  private final Map<String, Bucket> adminBuckets = new ConcurrentHashMap<>();

  private static final Bandwidth AUTH_LIMIT = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
  private static final Bandwidth API_LIMIT = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
  private static final Bandwidth ADMIN_LIMIT = Bandwidth.classic(200, Refill.intervally(200, Duration.ofMinutes(1)));

  private Bucket resolveBucket(Map<String, Bucket> cache, Bandwidth limit, String key) {
    return cache.computeIfAbsent(key, k -> Bucket.builder().addLimit(limit).build());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    if (!enabled) {
      chain.doFilter(request, response);
      return;
    }

    String path = request.getRequestURI();
    String ip = request.getRemoteAddr();
    String userId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : ip;

    Bucket bucket;
    if (path.startsWith("/api/v1/auth/")) {
      bucket = resolveBucket(authBuckets, AUTH_LIMIT, ip);
    } else if (path.startsWith("/api/v1/admin/")) {
      bucket = resolveBucket(adminBuckets, ADMIN_LIMIT, userId);
    } else if (path.startsWith("/api/v1/")) {
      bucket = resolveBucket(apiBuckets, API_LIMIT, userId);
    } else {
      chain.doFilter(request, response);
      return;
    }

    if (bucket.tryConsume(1)) {
      chain.doFilter(request, response);
    } else {
      response.setStatus(429);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(response.getOutputStream(),
          ApiResponse.error(42900, "请求太频繁"));
    }
  }
}
