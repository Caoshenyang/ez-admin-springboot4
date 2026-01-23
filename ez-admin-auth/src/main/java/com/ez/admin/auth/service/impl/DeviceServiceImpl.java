package com.ez.admin.auth.service.impl;

import com.ez.admin.auth.model.DeviceInfo;
import com.ez.admin.auth.service.DeviceService;
import com.ez.admin.common.framework.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 设备管理服务实现类
 * <p>
 * 使用 Redis 存储设备信息和 Refresh Token
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Slf4j
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private RedisCache redisCache;

    /**
     * 设备信息过期时间（天）
     * <p>
     * 设备信息在 Redis 中保留 30 天
     * </p>
     */
    private static final long DEVICE_EXPIRATION_DAYS = 30;

    @Override
    public void registerDevice(DeviceInfo deviceInfo) {
        log.debug("注册设备: userId={}, deviceId={}", deviceInfo.getUserId(), deviceInfo.getDeviceId());

        String key = getDeviceKey(deviceInfo.getUserId(), deviceInfo.getDeviceId());

        // 设置默认状态
        if (deviceInfo.getStatus() == null) {
            deviceInfo.setStatus("ACTIVE");
        }

        // 设置当前时间
        long currentTime = System.currentTimeMillis() / 1000;
        if (deviceInfo.getLoginTime() == null) {
            deviceInfo.setLoginTime(currentTime);
        }
        if (deviceInfo.getLastActiveTime() == null) {
            deviceInfo.setLastActiveTime(currentTime);
        }

        // 存储到 Redis（Hash 结构）
        redisCache.hSet(
                getUserDevicesKey(deviceInfo.getUserId()),
                deviceInfo.getDeviceId(),
                deviceInfo
        );

        // 设置过期时间
        redisCache.expire(
                getUserDevicesKey(deviceInfo.getUserId()),
                DEVICE_EXPIRATION_DAYS,
                TimeUnit.DAYS
        );
    }

    @Override
    public DeviceInfo getDevice(Long userId, String deviceId) {
        log.debug("获取设备信息: userId={}, deviceId={}", userId, deviceId);

        Object deviceObj = redisCache.hGet(getUserDevicesKey(userId), deviceId);
        if (deviceObj == null) {
            return null;
        }

        // 这里 Redis 会自动反序列化为 DeviceInfo 对象
        return (DeviceInfo) deviceObj;
    }

    @Override
    public Map<String, DeviceInfo> getUserDevices(Long userId) {
        log.debug("获取用户所有设备: userId={}", userId);

        Map<Object, Object> deviceMap = redisCache.hGetAll(getUserDevicesKey(userId));

        Map<String, DeviceInfo> result = new HashMap<>();
        deviceMap.forEach((key, value) -> {
            if (value instanceof DeviceInfo) {
                result.put((String) key, (DeviceInfo) value);
            }
        });

        return result;
    }

    @Override
    public List<DeviceInfo> getUserDeviceList(Long userId) {
        log.debug("获取用户设备列表: userId={}", userId);

        Map<String, DeviceInfo> deviceMap = getUserDevices(userId);

        return deviceMap.values().stream()
                .sorted((d1, d2) -> Long.compare(d2.getLastActiveTime(), d1.getLastActiveTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void kickOutDevice(Long userId, String deviceId) {
        log.info("踢出设备: userId={}, deviceId={}", userId, deviceId);

        DeviceInfo deviceInfo = getDevice(userId, deviceId);
        if (deviceInfo != null) {
            // 更新设备状态
            deviceInfo.setStatus("KICKED_OUT");
            registerDevice(deviceInfo);

            // 删除 Refresh Token
            if (deviceInfo.getRefreshToken() != null) {
                removeRefreshToken(deviceInfo.getRefreshToken());
            }
        }
    }

    @Override
    public void removeDevice(Long userId, String deviceId) {
        log.info("删除设备: userId={}, deviceId={}", userId, deviceId);

        DeviceInfo deviceInfo = getDevice(userId, deviceId);
        if (deviceInfo != null) {
            // 更新设备状态
            deviceInfo.setStatus("LOGGED_OUT");
            registerDevice(deviceInfo);

            // 删除 Refresh Token
            if (deviceInfo.getRefreshToken() != null) {
                removeRefreshToken(deviceInfo.getRefreshToken());
            }
        }
    }

    @Override
    public void kickOutAllDevices(Long userId) {
        log.info("踢出用户所有设备: userId={}", userId);

        Map<String, DeviceInfo> devices = getUserDevices(userId);
        devices.forEach((deviceId, deviceInfo) -> {
            deviceInfo.setStatus("KICKED_OUT");

            // 删除 Refresh Token
            if (deviceInfo.getRefreshToken() != null) {
                removeRefreshToken(deviceInfo.getRefreshToken());
            }
        });

        // 批量更新设备状态
        if (!devices.isEmpty()) {
            redisCache.hSet(
                    getUserDevicesKey(userId),
                    Arrays.toString(devices.keySet().toArray(new String[0])),
                    devices.values().toArray(new DeviceInfo[0])
            );
        }
    }

    @Override
    public void removeAllDevices(Long userId) {
        log.info("删除用户所有设备: userId={}", userId);

        Map<String, DeviceInfo> devices = getUserDevices(userId);

        // 删除所有 Refresh Token
        devices.values().forEach(deviceInfo -> {
            if (deviceInfo.getRefreshToken() != null) {
                removeRefreshToken(deviceInfo.getRefreshToken());
            }
        });

        // 删除所有设备记录
        redisCache.delete(getUserDevicesKey(userId));
    }

    @Override
    public void updateLastActiveTime(Long userId, String deviceId) {
        log.debug("更新设备最后活跃时间: userId={}, deviceId={}", userId, deviceId);

        DeviceInfo deviceInfo = getDevice(userId, deviceId);
        if (deviceInfo != null) {
            deviceInfo.updateLastActiveTime();
            registerDevice(deviceInfo);
        }
    }

    @Override
    public boolean isDeviceValid(Long userId, String deviceId) {
        DeviceInfo deviceInfo = getDevice(userId, deviceId);

        if (deviceInfo == null) {
            return false;
        }

        // 检查设备状态
        return "ACTIVE".equals(deviceInfo.getStatus());
    }

    @Override
    public void storeRefreshToken(String refreshToken, long expiration, Long userId, String deviceId) {
        log.debug("存储 Refresh Token: userId={}, deviceId={}", userId, deviceId);

        String key = getRefreshTokenKey(refreshToken);

        // 存储 Refresh Token 及关联信息
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("userId", userId);
        tokenInfo.put("deviceId", deviceId);
        tokenInfo.put("expiration", expiration);

        redisCache.set(key, tokenInfo, expiration);
    }

    @Override
    public boolean validateRefreshToken(String refreshToken, Long userId, String deviceId) {
        log.debug("验证 Refresh Token: userId={}, deviceId={}", userId, deviceId);

        String key = getRefreshTokenKey(refreshToken);
        Object tokenObj = redisCache.get(key);

        if (tokenObj == null) {
            return false;
        }

        if (tokenObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> tokenInfo = (Map<String, Object>) tokenObj;

            // 验证用户ID和设备ID
            return Objects.equals(userId, tokenInfo.get("userId")) &&
                    Objects.equals(deviceId, tokenInfo.get("deviceId"));
        }

        return false;
    }

    @Override
    public void removeRefreshToken(String refreshToken) {
        log.debug("删除 Refresh Token");

        String key = getRefreshTokenKey(refreshToken);
        redisCache.delete(key);
    }

    @Override
    public long getOnlineDeviceCount(Long userId) {
        Map<String, DeviceInfo> devices = getUserDevices(userId);

        return devices.values().stream()
                .filter(d -> "ACTIVE".equals(d.getStatus()))
                .count();
    }

    // =============================  私有方法  =============================

    /**
     * 获取用户设备列表的 Redis Key
     *
     * @param userId 用户ID
     * @return Redis Key
     */
    private String getUserDevicesKey(Long userId) {
        return DEVICE_KEY_PREFIX + userId;
    }

    /**
     * 获取单个设备的 Redis Key
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return Redis Key
     */
    private String getDeviceKey(Long userId, String deviceId) {
        return getUserDevicesKey(userId) + ":" + deviceId;
    }

    /**
     * 获取 Refresh Token 的 Redis Key
     *
     * @param refreshToken Refresh Token
     * @return Redis Key
     */
    private String getRefreshTokenKey(String refreshToken) {
        return REFRESH_TOKEN_KEY_PREFIX + refreshToken;
    }
}
