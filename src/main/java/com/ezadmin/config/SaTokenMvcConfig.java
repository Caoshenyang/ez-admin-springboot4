package com.ezadmin.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * 类名: SaTokenMvcConfig
 * 功能描述: mvc 相关配置类
 *
 * @author shenyang
 * @since 2025/3/15 14:00
 */
@Slf4j
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)  // 判断是否引入了 WebMVC
@RequiredArgsConstructor
public class SaTokenMvcConfig implements WebMvcConfigurer {

    /**
     * 额外放行路径，支持在 application.yaml 中通过 security.extra-ignore-paths 配置
     */
    @Value("${security.extra-ignore-paths:}")
    private List<String> extraIgnorePaths = Collections.emptyList();

    private static final List<String> DEFAULT_IGNORE_PATHS = Arrays.asList(
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/favicon.ico",
        "/auth/**"
    );

    private List<String> excludePaths() {
        if (extraIgnorePaths == null || extraIgnorePaths.isEmpty()) {
            return DEFAULT_IGNORE_PATHS;
        }
        // 合并默认与额外配置，保持顺序并去重
        return Stream.concat(DEFAULT_IGNORE_PATHS.stream(), extraIgnorePaths.stream())
            .distinct()
            .toList();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter
            .match("/**")
            .notMatch(excludePaths())
            .check(r -> StpUtil.checkLogin()))).addPathPatterns("/**");
    }
}
