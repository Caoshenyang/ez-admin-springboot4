package com.ez.admin.auth.core.service;

import com.ez.admin.auth.core.model.DeviceInfo;

import java.util.List;
import java.util.Map;

/**
 * 设备管理服务接口
 * <p>
 * 负责设备的注册、查询、踢出等操作
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
public interface DeviceService {

    /**
     * Redis Key 前缀
     */
    String DEVICE_KEY_PREFIX = "auth:devices:";
    String REFRESH_TOKEN_KEY_PREFIX = "auth:refresh_tokens:";

    /**
     * 注册/更新设备信息
     *
     * @param deviceInfo 设备信息
     */
    void registerDevice(DeviceInfo deviceInfo);

    /**
     * 获取设备信息
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return 设备信息
     */
    DeviceInfo getDevice(Long userId, String deviceId);

    /**
     * 获取用户的所有设备
     *
     * @param userId 用户ID
     * @return 设备列表（Map 结构：deviceId -> DeviceInfo）
     */
    Map<String, DeviceInfo> getUserDevices(Long userId);

    /**
     * 获取用户的所有设备列表
     *
     * @param userId 用户ID
     * @return 设备列表
     */
    List<DeviceInfo> getUserDeviceList(Long userId);

    /**
     * 踢出设备（标记为已踢出）
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    void kickOutDevice(Long userId, String deviceId);

    /**
     * 删除设备
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    void removeDevice(Long userId, String deviceId);

    /**
     * 踢出用户的所有设备
     *
     * @param userId 用户ID
     */
    void kickOutAllDevices(Long userId);

    /**
     * 删除用户的所有设备
     *
     * @param userId 用户ID
     */
    void removeAllDevices(Long userId);

    /**
     * 更新设备最后活跃时间
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     */
    void updateLastActiveTime(Long userId, String deviceId);

    /**
     * 验证设备是否有效（未被踢出）
     *
     * @param userId   用户ID
     * @param deviceId 设备ID
     * @return true: 有效，false: 无效
     */
    boolean isDeviceValid(Long userId, String deviceId);

    /**
     * 存储 Refresh Token
     *
     * @param refreshToken  Refresh Token
     * @param expiration    过期时间（秒）
     * @param userId        用户ID
     * @param deviceId      设备ID
     */
    void storeRefreshToken(String refreshToken, long expiration, Long userId, String deviceId);

    /**
     * 验证 Refresh Token 是否有效
     *
     * @param refreshToken Refresh Token
     * @param userId       用户ID
     * @param deviceId     设备ID
     * @return true: 有效，false: 无效
     */
    boolean validateRefreshToken(String refreshToken, Long userId, String deviceId);

    /**
     * 删除 Refresh Token
     *
     * @param refreshToken Refresh Token
     */
    void removeRefreshToken(String refreshToken);

    /**
     * 获取在线设备数量
     *
     * @param userId 用户ID
     * @return 在线设备数量
     */
    long getOnlineDeviceCount(Long userId);
}
