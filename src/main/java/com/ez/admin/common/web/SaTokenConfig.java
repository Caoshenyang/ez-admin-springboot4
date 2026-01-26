package com.ez.admin.common.web;

import com.ez.admin.common.permission.SaPermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 权限校验配置
 * <p>
 * 配置权限校验拦截器，拦截所有需要权限校验的接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final SaPermissionInterceptor saPermissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(saPermissionInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有 API 接口
                .excludePathPatterns(
                        "/api/auth/login",           // 登录接口
                        "/api/auth/logout",          // 登出接口
                        "/api/install/**",           // 初始化接口
                        "/doc/**",                   // Swagger 文档
                        "/swagger-resources/**",    // Swagger 资源
                        "/v3/api-docs/**",          // OpenAPI 文档
                        "/error"                     // 错误页面
                );
    }
}
