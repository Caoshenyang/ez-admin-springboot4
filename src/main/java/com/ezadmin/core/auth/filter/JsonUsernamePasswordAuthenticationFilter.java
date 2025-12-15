package com.ezadmin.core.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * 自定义用户名密码登录过滤器，只接受 JSON 格式的用户名密码登录请求
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 09:42:04
 */
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        
        // 只接受JSON格式请求
        if (request.getContentType() == null || !request.getContentType().contains("application/json")) {
            throw new AuthenticationServiceException("Authentication content type not supported: " + request.getContentType());
        }
        
        try {
            // 从JSON请求体中获取用户名和密码
            // 使用TypeReference解决泛型未检查赋值警告
            Map<String, String> jsonBody = objectMapper.readValue(
                request.getInputStream(),
                new TypeReference<Map<String, String>>() {
                }
            );
            String username = jsonBody.get(this.getUsernameParameter());
            String password = jsonBody.get(this.getPasswordParameter());

            UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(username, password);

            // 设置认证详情
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to parse JSON authentication request", e);
        }
    }
}
