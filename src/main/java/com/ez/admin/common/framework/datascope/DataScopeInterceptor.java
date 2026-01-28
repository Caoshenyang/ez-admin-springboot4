package com.ez.admin.common.framework.datascope;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 数据权限拦截器
 * <p>
 * 在每个请求开始时从 Sa-Token Session 中恢复数据权限上下文
 * 在请求结束时清理数据权限上下文，避免 ThreadLocal 内存泄漏
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Slf4j
@Component
public class DataScopeInterceptor implements HandlerInterceptor {

    private static final String DATA_SCOPE_SESSION_KEY = "dataScopeInfo";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查用户是否登录
        if (StpUtil.isLogin()) {
            // 从 Sa-Token Session 中获取数据权限信息
            DataScopeInfo dataScopeInfo = (DataScopeInfo) StpUtil.getSession().get(DATA_SCOPE_SESSION_KEY);

            if (dataScopeInfo != null) {
                // 设置到 ThreadLocal 中，供数据权限拦截器使用
                DataScopeContext.setDataScopeInfo(dataScopeInfo);
                log.debug("数据权限上下文已从 Session 恢复：{}", dataScopeInfo);
            } else {
                log.warn("用户 {} 的数据权限信息在 Session 中不存在，请重新登录", StpUtil.getLoginIdAsString());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理 ThreadLocal，避免内存泄漏
        DataScopeContext.clear();
        log.debug("数据权限上下文已清理");
    }
}
