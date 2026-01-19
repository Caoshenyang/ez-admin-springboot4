package com.ez.admin.auth.core.controller;

import com.ez.admin.auth.api.dto.LoginRequest;
import com.ez.admin.auth.api.dto.TokenResponse;
import com.ez.admin.auth.api.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * <p>
 * 提供登录、刷新、登出等认证相关的 REST API
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    /**
     * 登录接口
     * <p>
     * 支持多种登录渠道（小程序、移动APP、Web等）
     * </p>
     *
     * @param request 登录请求
     * @return Token 响应
     */
    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        log.info("用户登录: channelType={}, deviceId={}", request.getChannelType(), request.getDeviceId());

        TokenResponse response = authService.login(request);

        log.info("登录成功: userId={}, deviceId={}", response.getUserId(), request.getDeviceId());
        return response;
    }

    /**
     * 刷新 Token 接口
     * <p>
     * 使用 Refresh Token 刷新 Access Token
     * </p>
     *
     * @param requestBody 请求体（包含 refreshToken 和 deviceId）
     * @return 新的 Token 响应
     */
    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");
        String deviceId = requestBody.get("deviceId");

        log.info("刷新 Token: deviceId={}", deviceId);

        TokenResponse response = authService.refreshToken(refreshToken, deviceId);

        log.info("Token 刷新成功: userId={}, deviceId={}", response.getUserId(), deviceId);
        return response;
    }

    /**
     * 登出接口
     * <p>
     * 清除当前设备的 Token 和设备记录
     * </p>
     *
     * @param requestBody 请求体（包含 userId 和 deviceId）
     * @return 操作结果
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestBody Map<String, String> requestBody) {
        Long userId = Long.parseLong(requestBody.get("userId"));
        String deviceId = requestBody.get("deviceId");

        log.info("用户登出: userId={}, deviceId={}", userId, deviceId);

        authService.logout(userId, deviceId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登出成功");

        return result;
    }

    /**
     * 踢出设备接口
     * <p>
     * 强制指定设备下线
     * </p>
     *
     * @param requestBody 请求体（包含 userId 和 deviceId）
     * @return 操作结果
     */
    @PostMapping("/kickout")
    public Map<String, Object> kickOutDevice(@RequestBody Map<String, String> requestBody) {
        Long userId = Long.parseLong(requestBody.get("userId"));
        String deviceId = requestBody.get("deviceId");

        log.info("踢出设备: userId={}, deviceId={}", userId, deviceId);

        authService.kickOutDevice(userId, deviceId);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "设备已踢出");

        return result;
    }

    /**
     * 验证 Token 接口
     * <p>
     * 验证 Access Token 是否有效
     * </p>
     *
     * @param request HTTP 请求（从 Header 中获取 Token）
     * @return 验证结果
     */
    @GetMapping("/validate")
    public Map<String, Object> validateToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        boolean isValid = authService.validateToken(token);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("valid", isValid);

        if (isValid) {
            Long userId = authService.getUserIdFromToken(token);
            result.put("userId", userId);
        }

        return result;
    }

    /**
     * 获取当前用户信息
     * <p>
     * 从 Token 中解析用户ID
     * </p>
     *
     * @param request HTTP 请求（从 Header 中获取 Token）
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);

        if (!authService.validateToken(token)) {
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "Token 无效");

            return result;
        }

        Long userId = authService.getUserIdFromToken(token);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("userId", userId);

        return result;
    }

    // =============================  私有方法  =============================

    /**
     * 从 HTTP 请求中提取 Token
     * <p>
     * 支持两种格式：
     * 1. Authorization: Bearer {token}
     * 2. Authorization: {token}
     * </p>
     *
     * @param request HTTP 请求
     * @return Token 字符串
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return bearerToken;
    }
}
