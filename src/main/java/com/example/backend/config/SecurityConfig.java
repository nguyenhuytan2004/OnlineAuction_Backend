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

            // 1. NHÓM CÔNG KHAI (Không cần đăng nhập)
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/ws/**",
                "/api/payment/payos/webhook-handler")
            .permitAll()
            // Cho phép khách xem sản phẩm, danh mục và đánh giá (Chỉ cho phép GET)
            .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/ratings/reviewee/**")
            .permitAll()

            // 2. NHÓM QUYỀN SELLER (Hành động của người bán)
            .requestMatchers(HttpMethod.POST, "/api/products").hasRole("SELLER")
            .requestMatchers(HttpMethod.PATCH, "/api/products/*/append-description").hasRole("SELLER")
            .requestMatchers(
                "/api/user-profile/active-products",
                "/api/user-profile/sold-products",
                "/api/ratings/buyer",
                "/api/auction-results/product/*/cancel",
                "/api/orders/*/confirm-payment")
            .hasRole("SELLER")

            // 3. NHÓM QUYỀN CHUNG (Yêu cầu đăng nhập - Mọi role)
            .requestMatchers(HttpMethod.POST, "/api/products/*/questions", "/api/payment/payos/create-payment-link")
            .hasAnyRole("BIDDER", "SELLER", "ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/products/*/buy-now")
            .hasAnyRole("BIDDER", "SELLER", "ADMIN")

            // 4. NHÓM QUYỀN ADMIN (Quản trị viên)
            .requestMatchers("/api/users/**").hasRole("ADMIN")
            .requestMatchers("/api/categories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

            // 5. CÁC REQUEST CÒN LẠI (Fallback)
            .anyRequest().authenticated())

        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
