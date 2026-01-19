package com.ez.admin.auth.core.service;

import com.ez.admin.auth.core.model.DeviceInfo;

import java.util.Map;

/**
 * Token 服务接口
 * <p>
 * 负责 Token 的签发、刷新、验证等操作
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
public interface TokenService {

    /**
     * 生成 Access Token
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param claims   自定义载荷
     * @return Access Token
     */
    String generateAccessToken(Long userId, String deviceId, Map<String, Object> claims);

    /**
     * 生成 Refresh Token
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return Refresh Token
     */
    String generateRefreshToken(Long userId, String deviceId);

    /**
     * 生成 Device Token（用于"记住登录"）
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return Device Token
     */
    String generateDeviceToken(Long userId, String deviceId);

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return true: 有效，false: 无效
     */
    boolean validateToken(String token);

    /**
     * 从 Token 中解析用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);

    /**
     * 从 Token 中解析设备ID
     *
     * @param token JWT Token
     * @return 设备ID
     */
    String getDeviceIdFromToken(String token);

    /**
     * 获取 Token 过期时间（Unix 时间戳，秒）
     *
     * @param token JWT Token
     * @return 过期时间
     */
    Long getExpirationTime(String token);

    /**
     * 检查 Token 是否即将过期（5分钟内）
     *
     * @param token JWT Token
     * @return true: 即将过期，false: 未过期
     */
    boolean isTokenExpiringSoon(String token);
}
