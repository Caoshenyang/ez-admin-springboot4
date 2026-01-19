package com.ez.admin.auth.core.service.impl;

import com.ez.admin.auth.api.channel.AuthenticationChannel;
import com.ez.admin.auth.api.dto.LoginRequest;
import com.ez.admin.auth.api.dto.TokenResponse;
import com.ez.admin.auth.api.enums.ChannelType;
import com.ez.admin.auth.api.enums.TokenType;
import com.ez.admin.auth.api.exception.AuthenticationException;
import com.ez.admin.auth.api.service.IAuthService;
import com.ez.admin.auth.core.model.DeviceInfo;
import com.ez.admin.auth.core.service.DeviceService;
import com.ez.admin.auth.core.service.TokenService;
import com.ez.admin.framework.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * <p>
 * 实现统一的认证、刷新、登出等核心功能
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final TokenService tokenService;
    private final DeviceService deviceService;
    private final JwtTokenProvider jwtTokenProvider;
    private final List<AuthenticationChannel> authenticationChannels;

    /**
     * 渠道适配器缓存（按渠道类型索引）
     */
    private Map<ChannelType, AuthenticationChannel> channelMap;

    @Override
    public TokenResponse login(LoginRequest request) {
        // 1. 解析渠道类型
        ChannelType channelType;
        try {
            channelType = ChannelType.valueOf(request.getChannelType());
        } catch (IllegalArgumentException e) {
            log.warn("不支持的登录渠道: {}", request.getChannelType());
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.INVALID_CHANNEL,
                    "不支持的登录渠道: " + request.getChannelType()
            );
        }

        // 2. 获取对应的认证渠道适配器
        AuthenticationChannel channel = getChannel(channelType);
        if (channel == null) {
            log.warn("认证渠道适配器未实现: {}", channelType);
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.INVALID_CHANNEL,
                    "认证渠道适配器未实现: " + channelType
            );
        }

        // 3. 验证请求凭证
        if (!channel.validateCredentials(request)) {
            log.warn("登录凭证不完整: channel={}", channelType);
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.INVALID_CREDENTIALS,
                    "登录凭证不完整"
            );
        }

        // 4. 执行认证
        String userIdStr;
        try {
            userIdStr = channel.authenticate(request);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("认证失败: channel={}, error={}", channelType, e.getMessage(), e);
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.AUTHENTICATION_FAILED,
                    "认证失败"
            );
        }

        Long userId = Long.valueOf(userIdStr);

        // 5. 生成 Token
        TokenResponse response;
        if (Boolean.TRUE.equals(request.getRememberMe())) {
            // 记住登录：生成 Device Token（30天有效期）
            response = generateTokensWithDevice(userId, request.getDeviceId(), null);
        } else {
            // 普通登录：生成 Access Token + Refresh Token
            response = generateTokens(userId, request.getDeviceId(), null);
        }

        // 6. 注册/更新设备信息
        DeviceInfo deviceInfo = DeviceInfo.builder()
                .userId(userId)
                .deviceId(request.getDeviceId())
                .deviceName(request.getDeviceName() != null ? request.getDeviceName() : "Unknown Device")
                .refreshToken(response.getRefreshToken())
                .deviceToken(response.getDeviceToken())
                .build();

        deviceService.registerDevice(deviceInfo);

        log.info("登录成功: channel={}, userId={}, deviceId={}", channelType, userId, request.getDeviceId());
        return response;
    }

    /**
     * 获取认证渠道适配器
     *
     * @param channelType 渠道类型
     * @return 渠道适配器，如果不存在则返回 null
     */
    private AuthenticationChannel getChannel(ChannelType channelType) {
        if (channelMap == null) {
            // 懒加载：第一次访问时构建缓存
            channelMap = authenticationChannels.stream()
                    .collect(Collectors.toMap(
                            AuthenticationChannel::getSupportedChannel,
                            Function.identity()
                    ));
        }
        return channelMap.get(channelType);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken, String deviceId) {
        log.info("刷新 Token: deviceId={}", deviceId);

        // 1. 验证 Refresh Token 是否有效
        if (!tokenService.validateToken(refreshToken)) {
            log.warn("Refresh Token 无效或已过期");
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.REFRESH_TOKEN_INVALID,
                    "Refresh Token 无效或已过期"
            );
        }

        // 2. 从 Refresh Token 中解析用户ID
        Long userId = tokenService.getUserIdFromToken(refreshToken);
        if (userId == null) {
            log.warn("无法从 Refresh Token 中解析用户ID");
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.REFRESH_TOKEN_INVALID,
                    "Refresh Token 无效"
            );
        }

        // 3. 验证设备是否有效
        if (!deviceService.isDeviceValid(userId, deviceId)) {
            log.warn("设备无效或已被踢出: userId={}, deviceId={}", userId, deviceId);
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.DEVICE_NOT_AUTHORIZED,
                    "设备无效或已被踢出"
            );
        }

        // 4. 验证 Refresh Token 是否匹配该设备
        if (!deviceService.validateRefreshToken(refreshToken, userId, deviceId)) {
            log.warn("Refresh Token 与设备不匹配: userId={}, deviceId={}", userId, deviceId);
            throw new AuthenticationException(
                    AuthenticationException.ErrorCodes.REFRESH_TOKEN_INVALID,
                    "Refresh Token 无效"
            );
        }

        // 5. 删除旧的 Refresh Token
        deviceService.removeRefreshToken(refreshToken);

        // 6. 生成新的 Access Token 和 Refresh Token
        TokenResponse response = generateTokens(userId, deviceId, null);

        // 7. 更新设备信息
        DeviceInfo deviceInfo = deviceService.getDevice(userId, deviceId);
        if (deviceInfo != null) {
            deviceInfo.setRefreshToken(response.getRefreshToken());
            deviceInfo.updateLastActiveTime();
            deviceService.registerDevice(deviceInfo);
        }

        log.info("Token 刷新成功: userId={}, deviceId={}", userId, deviceId);
        return response;
    }

    @Override
    public void logout(Long userId, String deviceId) {
        log.info("用户登出: userId={}, deviceId={}", userId, deviceId);

        // 删除设备记录（标记为已登出）
        deviceService.removeDevice(userId, deviceId);
    }

    @Override
    public void kickOutDevice(Long userId, String deviceId) {
        log.info("踢出设备: userId={}, deviceId={}", userId, deviceId);

        // 踢出设备
        deviceService.kickOutDevice(userId, deviceId);
    }

    @Override
    public void kickOutAllDevices(Long userId) {
        log.info("踢出用户所有设备: userId={}", userId);

        // 踢出所有设备
        deviceService.kickOutAllDevices(userId);
    }

    @Override
    public boolean validateToken(String accessToken) {
        return tokenService.validateToken(accessToken);
    }

    @Override
    public Long getUserIdFromToken(String accessToken) {
        return tokenService.getUserIdFromToken(accessToken);
    }

    // =============================  私有方法  =============================

    /**
     * 生成 Token 响应
     * <p>
     * 包含 Access Token、Refresh Token、Device Token（可选）
     * </p>
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param claims   自定义载荷（可选）
     * @return Token 响应
     */
    protected TokenResponse generateTokens(Long userId, String deviceId, java.util.Map<String, Object> claims) {
        // 1. 生成 Access Token
        String accessToken = tokenService.generateAccessToken(userId, deviceId, claims);
        Long accessTokenExpiresIn = tokenService.getExpirationTime(accessToken);

        // 2. 生成 Refresh Token
        String refreshToken = tokenService.generateRefreshToken(userId, deviceId);
        Long refreshTokenExpiresIn = tokenService.getExpirationTime(refreshToken);

        // 3. 构建 Token 响应
        TokenResponse response = TokenResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .userId(userId)
                .build();

        return response;
    }

    /**
     * 生成带 Device Token 的 Token 响应
     * <p>
     * 用于"记住登录"功能
     * </p>
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @param claims   自定义载荷（可选）
     * @return Token 响应
     */
    protected TokenResponse generateTokensWithDevice(Long userId, String deviceId, java.util.Map<String, Object> claims) {
        // 1. 生成基础 Token
        TokenResponse response = generateTokens(userId, deviceId, claims);

        // 2. 生成 Device Token
        String deviceToken = tokenService.generateDeviceToken(userId, deviceId);
        Long deviceTokenExpiresIn = tokenService.getExpirationTime(deviceToken);

        // 3. 设置 Device Token
        response.setDeviceToken(deviceToken);
        response.setDeviceTokenExpiresIn(deviceTokenExpiresIn);

        return response;
    }
}
