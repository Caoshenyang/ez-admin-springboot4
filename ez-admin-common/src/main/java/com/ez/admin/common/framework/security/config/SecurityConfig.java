package com.ez.admin.common.framework.security.config;

import com.ez.admin.framework.security.jwt.JwtTokenProvider;
import com.ez.admin.framework.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 * <p>
 * 基于 Spring Security 7.x 配置，支持 JWT Token 认证
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * 密码编码器
     * <p>
     * 使用 BCrypt 强哈希算法，安全性高
     * </p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT 认证过滤器
     * <p>
     * 从请求头中提取 JWT Token 并验证，将认证信息设置到 SecurityContext 中
     * </p>
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * 安全过滤器链配置
     * <p>
     * Spring Security 7.x 使用 SecurityFilterChain Bean 方式配置
     * </p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
                .csrf(AbstractHttpConfigurer::disable)

                // 禁用 CORS（如需跨域请单独配置 CorsConfigurationSource）
                .cors(AbstractHttpConfigurer::disable)

                // 无状态会话管理（JWT 不需要 Session）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // 配置授权规则
                .authorizeHttpRequests(auth -> auth
                        // 认证相关接口公开访问
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**"
                        ).permitAll()

                        // Swagger 相关接口公开访问
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                // 添加 JWT 认证过滤器（在 UsernamePasswordAuthenticationFilter 之前）
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
