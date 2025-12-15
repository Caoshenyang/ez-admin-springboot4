package com.ezadmin.core.auth.handler;

import com.ezadmin.core.response.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 自定义认证入口点
 * 处理未认证的请求（如未登录用户访问受保护资源）
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 11:42:46
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JacksonJsonHttpMessageConverter converter;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Result<Void> result = Result.error(401, "未认证：" + authException.getMessage());
        converter.getMapper().writer().writeValue(response.getWriter(), result);
    }
}
