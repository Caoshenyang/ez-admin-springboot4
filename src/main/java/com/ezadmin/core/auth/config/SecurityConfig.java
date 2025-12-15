package com.ezadmin.core.auth.config;

import com.ezadmin.core.auth.filter.JsonUsernamePasswordAuthenticationFilter;
import com.ezadmin.core.auth.handler.CustomAccessDeniedHandler;
import com.ezadmin.core.auth.handler.CustomAuthenticationEntryPoint;
import com.ezadmin.core.auth.handler.LoginFailureHandler;
import com.ezadmin.core.auth.handler.LoginSuccessHandler;
import com.ezadmin.core.auth.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
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


/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity(debug = true)
//@EnableMethodSecurity // 开启方法级别的安全注解
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    // 认证成功处理器
    private final LoginSuccessHandler loginSuccessHandler;
    // 认证失败处理器
    private final LoginFailureHandler loginFailureHandler;

    // 自定义访问拒绝处理器
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    // 自定义认证入口点
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;


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
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            // 完全禁用所有安全功能
            .authorizeHttpRequests(authorize -> authorize
                // 登录接口允许匿名访问
                .requestMatchers("/auth/login").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)// 配置认证失败处理器
                .accessDeniedHandler(customAccessDeniedHandler) // 配置访问拒绝处理器
            )
            // 禁用CSRF保护
            .csrf(csrf -> csrf.disable())
            // 禁用表单登录
            .formLogin(form -> form.disable())
            // 禁用HTTP基本认证
            .httpBasic(basic -> basic.disable())
            // 禁用会话管理
            // 在SecurityConfig.java的filterChain方法中，将空的sessionManagement配置
            // .sessionManagement(session -> {}) 
            // 替换为：
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // 替换默认的UsernamePasswordAuthenticationFilter
            .addFilterAt(jsonAuthFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);



        return http.build();
    }


    /**
     * AuthenticationProvider 说明：
     * AuthenticationProvider是Spring Security中负责实际进行身份验证的核心接口
     * 它有一个authenticate()方法，接收Authentication对象，返回认证后的Authentication对象或抛出异常
     * 在实际应用中，您可以通过配置这个Bean来替代UserDetailsService方式进行认证
     * 它的默认实现是DaoAuthenticationProvider，它使用UserDetailsService获取用户信息并验证密码
     * 其实可以不配置，默认也是 DaoAuthenticationProvider, 从容器中自动注入 customUserDetailsService 和 passwordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        // DaoAuthenticationProvider是最常用的实现，它使用UserDetailsService获取用户信息并验证密码
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


    /**
     * JsonUsernamePasswordAuthenticationFilter 说明：
     * 自定义用户名密码登录过滤器，用于处理 JSON 格式的用户名密码登录请求
     */
    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonAuthFilter(AuthenticationManager authenticationManager) {
        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        // 设置登录URL为 /auth/login
        filter.setFilterProcessesUrl("/auth/login");
        // 配置认证成功处理器
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        // 配置认证失败处理器
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        return filter;
    }


}
