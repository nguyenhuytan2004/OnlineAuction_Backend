package com.example.backend.service.core;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userId;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userId = jwtService.extractUserId(jwt);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = customUserDetailsService.loadUserById(Integer.valueOf(userId));

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    if (!userDetails.isEnabled()) {
                        filterChain.doFilter(request, response);
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException e) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // Các đường dẫn công khai với method GET
        boolean isPublicGetRequest = method.equalsIgnoreCase("GET")
                && (path.equals("/api/products")
                        || path.matches("/api/products/\\d+")
                        || path.matches("/api/products/\\d+/bids")
                        || path.equals("/api/products/top-5-ending-soon")
                        || path.equals("/api/products/top-5-most-auctioned")
                        || path.equals("/api/products/top-5-highest-priced")
                        || path.matches("/api/products/\\d+/top-5-related")
                        || path.equals("/api/products/full-text-search")
                        || path.matches("/api/products/category/\\d+")
                        || path.matches("/api/products/category/\\d+/full-text-search")
                        || path.matches("/api/products/\\d+/questions")
                        || path.equals("/api/categories")
                        || path.startsWith("/api/categories/"));

        boolean isAlwaysPublic = path.equals("/api/auth/register")
                || path.equals("/api/auth/login")
                || path.startsWith("/v3/api-docs/")
                || path.startsWith("/swagger-ui/")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-resources/")
                || path.startsWith("/webjars/")
                || path.startsWith("/ws/");

        return isPublicGetRequest || isAlwaysPublic;
    }
}
