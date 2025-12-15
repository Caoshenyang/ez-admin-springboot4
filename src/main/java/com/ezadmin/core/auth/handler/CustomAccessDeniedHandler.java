package com.ezadmin.core.auth.handler;

import com.ezadmin.core.response.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 自定义权限不足处理器
 * 处理已认证但权限不足的请求
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 11:48:43
 */
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final JacksonJsonHttpMessageConverter converter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        Result<Void> result = Result.error(403, "权限不足：" + accessDeniedException.getMessage());
        converter.getMapper().writer().writeValue(response.getWriter(), result);
    }
}
