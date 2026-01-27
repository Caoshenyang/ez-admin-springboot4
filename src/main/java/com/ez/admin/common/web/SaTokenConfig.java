package com.ez.admin.common.web;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.common.cache.AdminCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * Sa-Token 权限校验配置
 * <p>
 * 配置 Sa-Token 拦截器，实现登录校验和基于 Redis 缓存的路由拦截式权限校验
 * </p>
 * <p>
 * 权限校验流程：
 * <ol>
 *   <li>从 Redis 缓存获取所有路由权限规则（避免每次查询数据库）</li>
 *   <li>匹配当前请求路径，查找对应的权限码</li>
 *   <li>如果找到了权限码，则校验用户是否拥有该权限</li>
 *   <li>如果没有找到权限码，说明该路由不需要权限控制，直接放行</li>
 * </ol>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final AdminCache adminCache;


    // Sa-Token 整合 jwt (Simple 简单模式)
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
                    // 1. 获取当前请求路径和方法
                    String path = SaHolder.getRequest().getRequestPath();
                    String method = SaHolder.getRequest().getMethod();

                    // 2. 从 Redis 获取所有的权限配置规则（缓存中，性能极高）
                    Map<String, String> routePermMap = adminCache.getRoutePermissionCache();

                    // 3. 构建当前请求的路由键：METHOD:PATH，如 "POST:/api/user"
                    String currentRouteKey = method + ":" + path;

                    // 4. 查找当前路由需要的权限码
                    String requiredPerm = routePermMap.get(currentRouteKey);

                    // 5. 如果找到了权限码，则进行权限校验
                    if (requiredPerm != null && !requiredPerm.isEmpty()) {
                        SaRouter.match(path).check(r -> {
                            // 先校验登录
                            StpUtil.checkLogin();
                            // 再校验权限
                            log.debug("路由权限校验：{} {} 需要权限 {}", method, path, requiredPerm);
                            StpUtil.checkPermission(requiredPerm);
                        });
                    } else {
                        // 如果没有找到权限码，只做登录校验
                        SaRouter.match(path).check(r -> {
                            StpUtil.checkLogin();
                            log.debug("路由无需权限控制（仅需登录）：{} {}", method, path);
                        });
                    }

                })).addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",          // 登录接口
                        "/api/auth/logout",         // 登出接口
                        "/api/install/**",          // 初始化接口
                        "/doc/**",                  // Swagger 文档
                        "/swagger-resources/**",    // Swagger 资源
                        "/v3/api-docs/**",          // OpenAPI 文档
                        "/error"                    // 错误页面
                );
    }
}
