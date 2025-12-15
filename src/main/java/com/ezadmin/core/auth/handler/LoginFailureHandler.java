package com.ezadmin.core.auth.handler;

import com.ezadmin.core.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 登录失败处理器
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 11:27:09
 */
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final JacksonJsonHttpMessageConverter converter;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        String errorMessage;
        // 对于用户名和密码相关的认证失败，统一返回模糊错误信息，防止爆破攻击
        if (exception instanceof BadCredentialsException || 
            exception instanceof UsernameNotFoundException || 
            (exception.getCause() instanceof UsernameNotFoundException)) {
            // 用户名或密码错误时，统一返回模糊信息
            errorMessage = "用户名或密码错误";
        } else {
            // 其他类型的认证异常，使用原始错误信息
            errorMessage = exception.getMessage();
        }

        // 使用统一的Result格式返回错误响应
        Result<Void> result = Result.error(errorMessage);
        converter.getMapper().writer().writeValue(response.getWriter(), result);
    }
}
