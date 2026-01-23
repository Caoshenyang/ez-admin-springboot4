package com.ez.admin.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备信息
 * <p>
 * 存储在 Redis 中的设备详细信息
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 设备 ID（唯一标识）
     */
    private String deviceId;

    /**
     * 设备类型（MINI_PROGRAM, MOBILE_APP, WEB_ADMIN, H5, PC_CLIENT）
     */
    private String deviceType;

    /**
     * 设备名称（如 "iPhone 14 Pro"、"Windows PC"）
     */
    private String deviceName;

    /**
     * 登录时间（Unix 时间戳，秒）
     */
    private Long loginTime;

    /**
     * 最后活跃时间（Unix 时间戳，秒）
     */
    private Long lastActiveTime;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * Device Token（可选，用于"记住登录"）
     */
    private String deviceToken;

    /**
     * 客户端 IP 地址
     */
    private String ipAddress;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 设备状态（ACTIVE: 活跃, KICKED_OUT: 被踢出, LOGGED_OUT: 已登出）
     */
    private String status;

    /**
     * 更新最后活跃时间
     */
    public void updateLastActiveTime() {
        this.lastActiveTime = System.currentTimeMillis() / 1000;
    }
}
