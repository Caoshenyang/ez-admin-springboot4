package com.ez.admin.framework.security.handler;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 无权限访问处理器
 * <p>
 * 当用户已认证但权限不足访问资源时触发
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {

    }

//    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Override
//    public void handle(HttpServletRequest request,
//                       HttpServletResponse response,
//                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        log.debug("无权限访问: {}, 异常: {}", request.getRequestURI(), accessDeniedException.getMessage());
//
//        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        response.setContentType("application/json;charset=UTF-8");
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("code", 403);
//        data.put("message", "无权限访问该资源");
//        data.put("path", request.getRequestURI());
//        data.put("timestamp", System.currentTimeMillis());
//
//        response.getWriter().write(objectMapper.writeValueAsString(data));
//    }
}
