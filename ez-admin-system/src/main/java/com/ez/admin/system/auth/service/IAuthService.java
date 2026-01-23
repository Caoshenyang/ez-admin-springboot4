package com.ez.admin.system.auth.service;

import com.ez.admin.system.auth.dto.LoginRequest;
import com.ez.admin.system.auth.dto.TokenResponse;

/**
 * 认证服务接口
 * <p>
 * 定义统一认证中心的核心能力，包括登录、刷新、登出等
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
public interface IAuthService {

    /**
     * 登录
     * <p>
     * 根据不同的登录渠道进行认证，成功后返回 Token 信息
     * </p>
     *
     * @param request 登录请求
     * @return Token 响应
     * @throws com.ez.admin.common.exception.EzBusinessException 认证失败时抛出
     */
    TokenResponse login(LoginRequest request);

    /**
     * 刷新 Token
     * <p>
     * 使用 Refresh Token 刷新 Access Token
     * </p>
     *
     * @param refreshToken 刷新令牌
     * @param deviceId     设备 ID
     * @return 新的 Token 响应
     * @throws com.ez.admin.common.exception.EzBusinessException 刷新失败时抛出
     */
    TokenResponse refreshToken(String refreshToken, String deviceId);

    /**
     * 登出
     * <p>
     * 清除当前设备的 Token 和设备记录
     * </p>
     *
     * @param userId   用户 ID
     * @param deviceId 设备 ID
     */
    void logout(Long userId, String deviceId);

    /**
     * 踢出设备
     * <p>
     * 强制指定设备下线
     * </p>
     *
     * @param userId   用户 ID
     * @param deviceId 设备 ID
     */
    void kickOutDevice(Long userId, String deviceId);

    /**
     * 踢出所有设备
     * <p>
     * 强制用户的所有设备下线
     * </p>
     *
     * @param userId 用户 ID
     */
    void kickOutAllDevices(Long userId);

    /**
     * 验证 Token 是否有效
     * <p>
     * 用于中间件或拦截器中验证 Access Token
     * </p>
     *
     * @param accessToken 访问令牌
     * @return true: 有效，false: 无效
     */
    boolean validateToken(String accessToken);

    /**
     * 从 Token 中解析用户 ID
     * <p>
     * 用于获取当前登录用户的信息
     * </p>
     *
     * @param accessToken 访问令牌
     * @return 用户 ID
     */
    Long getUserIdFromToken(String accessToken);
}
