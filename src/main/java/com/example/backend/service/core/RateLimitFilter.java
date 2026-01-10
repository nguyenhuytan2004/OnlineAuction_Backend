package com.example.backend.service.core;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
  private final EndpointRateLimiter rateLimiter;

  public RateLimitFilter(EndpointRateLimiter rateLimiter) {
    this.rateLimiter = rateLimiter;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
          throws ServletException, IOException {

    String key = request.getRemoteAddr() + ":" + request.getRequestURI();
    Bandwidth limit = rateLimiter.getLimitFor(request.getRequestURI());

    Bucket bucket = cache.computeIfAbsent(key, k ->
            Bucket4j.builder().addLimit(limit).build()
    );

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      response.setStatus(429);
      response.getWriter().write("Too many requests for this endpoint");
    }
  }
}
