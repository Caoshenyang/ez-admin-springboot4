package com.ezadimn.core.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 开启方法级别的安全注解
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * AuthenticationManager 说明：
     * AuthenticationManager是Spring Security认证体系的入口点
     * 它协调多个AuthenticationProvider实例，尝试对认证请求进行处理
     * 如果一个AuthenticationProvider无法认证，它会尝试下一个
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        // 从配置中获取默认的AuthenticationManager
        // 如果配置了AuthenticationProvider，它会自动使用这些Provider
        return authConfig.getAuthenticationManager();
    }

    /**
     * 完全允许所有请求，不进行任何认证和授权
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 完全禁用所有安全功能
            .authorizeHttpRequests(authorize -> authorize
                // 允许所有请求访问
                .anyRequest().permitAll()
            )
            // 禁用CSRF保护
            .csrf(csrf -> csrf.disable())
            // 禁用表单登录
            .formLogin(form -> form.disable())
            // 禁用HTTP基本认证
            .httpBasic(basic -> basic.disable())
            // 禁用会话管理
            .sessionManagement(session -> session.disable());

        return http.build();
    }


    /**
     * AuthenticationProvider 说明：
     * AuthenticationProvider是Spring Security中负责实际进行身份验证的核心接口
     * 它有一个authenticate()方法，接收Authentication对象，返回认证后的Authentication对象或抛出异常
     *
     * 以下是一个示例实现（当前被注释掉，因为我们使用了最小化配置）
     * 在实际应用中，您可以通过配置这个Bean来替代UserDetailsService方式进行认证
     */
    /*
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        // DaoAuthenticationProvider是最常用的实现，它使用UserDetailsService获取用户信息并验证密码
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    */


}
