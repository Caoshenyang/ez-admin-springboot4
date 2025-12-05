package com.ezadimn.modules.system.service;

import com.ezadimn.modules.system.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-05 15:31:51
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;


    /**
     * 登录方法
     */
    public String login(LoginDTO loginDTO) {
        // 1. 创建未认证的 Authentication 对象
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        // 2. 调用 AuthenticationManager 进行认证（内部会调用我们的 UserDetailsService）
        Authentication authentication = authenticationManager.authenticate(authToken);
        // 3. 认证成功，将认证信息存入 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 4. 生成响应（这里可以根据需要生成 JWT Token 或返回用户信息）
        String token = UUID.randomUUID().toString();
        return token;

    }
}
