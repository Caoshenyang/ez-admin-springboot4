package com.ez.admin.common.framework.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证失败处理器
 * <p>
 * 当用户未认证（未登录或 Token 无效）访问受保护资源时触发
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

    }

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public void commence(HttpServletRequest request,
//                         HttpServletResponse response,
//                         AuthenticationException authException) throws IOException, ServletException {
//        log.debug("未认证访问: {}, 异常: {}", request.getRequestURI(), authException.getMessage());
//
//        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("code", 401);
//        data.put("message", "未认证，请先登录");
//        data.put("path", request.getRequestURI());
//        data.put("timestamp", System.currentTimeMillis());
//
//        response.getWriter().write(objectMapper.writeValueAsString(data));
//    }
}
