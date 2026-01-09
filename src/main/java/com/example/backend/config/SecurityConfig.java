package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.backend.service.core.CustomUserDetailsService;
import com.example.backend.service.core.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!dev")
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final JwtAuthFilter jwtAuthFilter;
  private final CorsConfigurationSource corsConfigurationSource;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authProvider())
        .authorizeHttpRequests(auth -> auth

            // 1. Công khai hoàn toàn (Auth, Swagger, Websocket)
            .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/ws/**")
            .permitAll()

            // 2. Quyền ADMIN (Quản lý hệ thống - Phải đặt lên trước các quyền chung)
            .requestMatchers("/api/users/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/categories/**").hasRole("ADMIN")
            // ADMIN có quyền PATCH/DELETE mọi product
            .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

            // 3. Quyền SELLER (Hành động đặc thù của người bán)
            .requestMatchers(
                "/api/user-profile/active-products",
                "/api/user-profile/sold-products",
                "/api/ratings/buyer",
                "/api/auction-results/product/*/cancel",
                "/api/orders/*/confirm-payment")
            .hasRole("SELLER")
            .requestMatchers(HttpMethod.POST, "/api/products").hasRole("SELLER")
            .requestMatchers(HttpMethod.PATCH, "/api/products/*/append-description").hasRole("SELLER")

            // 4. Quyền chung (Yêu cầu đăng nhập)
            .requestMatchers(HttpMethod.POST, "/api/products/*/questions").hasAnyRole("SELLER", "BIDDER", "ADMIN")

            // 5. Công khai cho khách xem (READ-ONLY)
            .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/ratings/reviewee/**")
            .permitAll()

            // 6. Tất cả các request còn lại phải đăng nhập
            .anyRequest().authenticated())

        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
