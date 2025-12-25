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

            .requestMatchers(
                "/api/auth/register",
                "/api/auth/login",
                "/api/auth/verify-email",
                "/api/auth/forgot-password",
                "/api/auth/reset-password",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/webjars/**",
                "/ws/**")
            .permitAll()

            .requestMatchers(HttpMethod.GET,
                "/api/products",
                "/api/products/{product_id}",
                "/api/products/{product_id}/bids",
                "/api/products/top-5-ending-soon",
                "/api/products/top-5-most-auctioned",
                "/api/products/top-5-highest-priced",
                "/api/products/{product_id}/top-5-related",
                "/api/products/full-text-search",
                "/api/products/category/{category_id}",
                "/api/products/category/{category_id}/full-text-search",
                "/api/products/{product_id}/questions",
                "/api/categories",
                "/api/categories/**")
            .permitAll()

            .requestMatchers(HttpMethod.POST,
                "/api/products/{product_id}/questions")
            .hasAnyRole("SELLER", "BIDDER")
            .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("SELLER", "BIDDER")

            .requestMatchers(
                "/api/user-profile/active-products",
                "/api/user-profile/sold-products",
                "/api/ratings/buyer",
                "/api/auction-results/product/*/cancel",
                "/api/products/*/append-description", "/api/products")
            .hasRole("SELLER")

            .requestMatchers("/api/categories/**").hasRole("ADMIN")
            .requestMatchers("/api/products/**").hasRole("ADMIN")
            .requestMatchers("/api/users/**").hasRole("ADMIN")

            .anyRequest().authenticated())

        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
