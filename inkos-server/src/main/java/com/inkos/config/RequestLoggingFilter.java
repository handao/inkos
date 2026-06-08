package com.inkos.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(2)
public class RequestLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
    ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

    String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    MDC.put("requestId", requestId);

    long start = System.currentTimeMillis();
    try {
      chain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      long duration = System.currentTimeMillis() - start;
      String path = request.getRequestURI();
      String method = request.getMethod();
      int status = wrappedResponse.getStatus();
      String userId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "-";

      String query = request.getQueryString();
      String fullPath = query != null ? path + "?" + query : path;

      if (!path.startsWith("/static/")) {
        log.info("{} {} {} {} {}ms {}", requestId, method, status, fullPath, duration, userId);
      }

      MDC.remove("requestId");
      wrappedResponse.copyBodyToResponse();
    }
  }
}
