package com.ez.admin.auth.core.enums;

/**
 * Token 类型枚举
 * <p>
 * 定义三种 Token 类型的用途和有效期策略
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
public enum TokenType {

    /**
     * 访问令牌（Access Token）
     * <p>
     * 用途：用于 API 访问认证，携带在请求头中
     * 有效期：短期（15分钟）
     * 存储：JWT 无状态存储
     * </p>
     */
    ACCESS_TOKEN("ACCESS_TOKEN", 15 * 60, "访问令牌"),

    /**
     * 刷新令牌（Refresh Token）
     * <p>
     * 用途：用于刷新 Access Token，当 Access Token 过期时使用
     * 有效期：中期（7天）
     * 存储：Redis 存储，绑定设备
     * </p>
     */
    REFRESH_TOKEN("REFRESH_TOKEN", 7 * 24 * 60 * 60, "刷新令牌"),

    /**
     * 设备令牌（Device Token）
     * <p>
     * 用途：用于"记住登录"功能，延长登录状态
     * 有效期：长期（30天）
     * 存储：Redis 存储，绑定设备
     * </p>
     */
    DEVICE_TOKEN("DEVICE_TOKEN", 30 * 24 * 60 * 60, "设备令牌");

    /**
     * Token 类型代码
     */
    private final String code;

    /**
     * 默认有效期（秒）
     */
    private final int defaultExpirationSeconds;

    /**
     * Token 描述
     */
    private final String description;

    TokenType(String code, int defaultExpirationSeconds, String description) {
        this.code = code;
        this.defaultExpirationSeconds = defaultExpirationSeconds;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public int getDefaultExpirationSeconds() {
        return defaultExpirationSeconds;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code Token 类型代码
     * @return Token 类型枚举
     */
    public static TokenType fromCode(String code) {
        for (TokenType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown token type: " + code);
    }
}
