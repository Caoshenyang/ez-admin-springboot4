package com.ez.admin.service.auth;

import com.ez.admin.dto.auth.req.LoginRequest;
import com.ez.admin.dto.auth.vo.LoginResponse;

/**
 * 认证服务接口
 *
 * @author ez-admin
 * @since 2026-01-23
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 token）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout();
}
