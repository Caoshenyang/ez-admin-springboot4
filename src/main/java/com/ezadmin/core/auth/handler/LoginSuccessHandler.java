package com.ezadmin.core.auth.handler;

import com.ezadmin.core.auth.security.CustomUserDetails;
import com.ezadmin.core.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 认证成功处理器
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 11:11:39
 */
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JacksonJsonHttpMessageConverter converter;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setStatus(HttpStatus.OK.value());
        // 使用统一的Result格式返回成功响应
        Result<String> result = Result.success("登录成功");
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            // 只返回必要的用户信息，避免敏感数据泄露
            result.setData(userDetails.getUsername());
        }
        converter.getMapper().writer().writeValue(response.getWriter(), result);
    }
}
