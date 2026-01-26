package com.ez.admin.common.permission;

import cn.dev33.satoken.stp.StpUtil;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.exception.EzBusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * Sa-Token 权限校验拦截器
 * <p>
 * 拦截所有请求，检查方法上的 @SaCheckPermission 注解
 * 如果注解存在，则校验用户是否具有对应权限
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Component
public class SaPermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 如果不是方法处理器（如静态资源），直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        SaCheckPermission annotation = handlerMethod.getMethodAnnotation(SaCheckPermission.class);

        // 2. 如果没有权限注解，直接放行
        if (annotation == null) {
            return true;
        }

        // 3. 检查是否需要登录
        if (annotation.login()) {
            // 如果未登录，Sa-Token 会自动抛出异常
            StpUtil.checkLogin();
        }

        // 4. 校验权限
        String permission = annotation.value();
        if (annotation.mode() == SaCheckPermission.SaMode.AND) {
            // AND 模式：需要所有权限
            StpUtil.checkPermission(permission);
        } else {
            // OR 模式：只需其中一个权限
            List<String> permissions = Arrays.asList(permission.split(","));
            boolean hasPermission = permissions.stream().anyMatch(p -> StpUtil.hasPermission(p));
            if (!hasPermission) {
                throw new EzBusinessException(ErrorCode.FORBIDDEN);
            }
        }

        return true;
    }
}
