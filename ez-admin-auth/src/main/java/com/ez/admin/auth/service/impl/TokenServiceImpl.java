package com.ez.admin.auth.service.impl;

import com.ez.admin.auth.service.TokenService;
import com.ez.admin.common.framework.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Token 服务实现类
 * <p>
 * 基于 JwtTokenProvider 实现 Token 的生成、解析、验证
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Token 过期预警时间（秒）
     * <p>
     * 当 Token 有效期小于此值时，视为即将过期
     * </p>
     */
    private static final long TOKEN_EXPIRING_SOON_THRESHOLD = 5 * 60; // 5分钟

    @Override
    public String generateAccessToken(Long userId, String deviceId, Map<String, Object> claims) {
        log.debug("生成 Access Token: userId={}, deviceId={}", userId, deviceId);

        // 构建载荷
        Map<String, Object> finalClaims = new HashMap<>();
        finalClaims.put("deviceId", deviceId);
        finalClaims.put("tokenType", "ACCESS_TOKEN");

        if (claims != null && !claims.isEmpty()) {
            finalClaims.putAll(claims);
        }

        // 生成 Token
        return jwtTokenProvider.generateAccessToken(String.valueOf(userId), finalClaims);
    }

    @Override
    public String generateRefreshToken(Long userId, String deviceId) {
        log.debug("生成 Refresh Token: userId={}, deviceId={}", userId, deviceId);

        // 构建载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("deviceId", deviceId);
        claims.put("tokenType", "REFRESH_TOKEN");

        // 生成 Token
        return jwtTokenProvider.generateRefreshToken(String.valueOf(userId), claims);
    }

    @Override
    public String generateDeviceToken(Long userId, String deviceId) {
        log.debug("生成 Device Token: userId={}, deviceId={}", userId, deviceId);

        // 构建载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("deviceId", deviceId);
        claims.put("tokenType", "DEVICE_TOKEN");

        // Device Token 有效期为 30 天
        long deviceTokenExpiration = 30 * 24 * 60 * 60; // 30天

        return jwtTokenProvider.generateToken(String.valueOf(userId), claims, deviceTokenExpiration);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            log.error("Token 验证失败", e);
            return false;
        }
    }

    @Override
    public Long getUserIdFromToken(String token) {
        try {
            String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            log.error("从 Token 中解析用户ID失败", e);
            return null;
        }
    }

    @Override
    public String getDeviceIdFromToken(String token) {
        try {
            Object deviceId = jwtTokenProvider.getClaim(token, "deviceId");
            return deviceId != null ? deviceId.toString() : null;
        } catch (Exception e) {
            log.error("从 Token 中解析设备ID失败", e);
            return null;
        }
    }

    @Override
    public Long getExpirationTime(String token) {
        try {
            return jwtTokenProvider.getExpirationTime(token);
        } catch (Exception e) {
            log.error("获取 Token 过期时间失败", e);
            return null;
        }
    }

    @Override
    public boolean isTokenExpiringSoon(String token) {
        try {
            Long expirationTime = getExpirationTime(token);
            if (expirationTime == null) {
                return false;
            }

            long currentTime = System.currentTimeMillis() / 1000;
            long timeLeft = expirationTime - currentTime;

            return timeLeft <= TOKEN_EXPIRING_SOON_THRESHOLD;
        } catch (Exception e) {
            log.error("检查 Token 是否即将过期失败", e);
            return false;
        }
    }
}
