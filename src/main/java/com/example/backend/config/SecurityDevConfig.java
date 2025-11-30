package com.example.backend.config;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev") // CHỈ chạy khi bật profile dev
public class SecurityDevConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            // Bypass hoàn toàn: luôn authenticated
            authentication.setAuthenticated(true);
            return authentication;
        };
    }

    @Bean
    public SecurityFilterChain filterChainDev(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(sess -> sess.disable())
                .securityMatcher("/**")
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
