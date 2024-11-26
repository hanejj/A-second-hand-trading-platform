package com.gajimarket.Gajimarket.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 인증 정보 허용
        config.addAllowedOriginPattern("*"); // 프론트엔드 Origin 허용
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH")); // 허용 메서드
        config.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        config.setMaxAge(3600L); // preflight 요청 캐싱 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 CORS 설정 적용
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(configurationSource())) // CORS 설정 추가
                .headers(headers -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))) // X-Frame-Options 설정

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함 (Stateless)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/report/**").permitAll() // /image/** 경로 인증 없이 허용
                        .requestMatchers("/image/**").permitAll() // /image/** 경로 인증 없이 허용
                        .requestMatchers("/product/**").permitAll() // /product/** 경로 인증 없이 허용
                        .anyRequest().authenticated()); // 나머지 요청은 인증 필요


        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

}
