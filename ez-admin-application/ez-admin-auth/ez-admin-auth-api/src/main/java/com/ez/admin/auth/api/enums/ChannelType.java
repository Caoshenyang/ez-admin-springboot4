package com.ez.admin.auth.api.enums;

/**
 * 登录渠道枚举
 * <p>
 * 用于区分不同的客户端登录渠道，支持多端适配
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
public enum ChannelType {

    /**
     * 微信小程序
     */
    MINI_PROGRAM("MINI_PROGRAM", "微信小程序"),

    /**
     * 移动 APP
     */
    MOBILE_APP("MOBILE_APP", "移动APP"),

    /**
     * Web 管理端
     */
    WEB_ADMIN("WEB_ADMIN", "Web管理端"),

    /**
     * H5 页面
     */
    H5("H5", "H5页面"),

    /**
     * PC 客户端
     */
    PC_CLIENT("PC_CLIENT", "PC客户端");

    /**
     * 渠道代码
     */
    private final String code;

    /**
     * 渠道描述
     */
    private final String description;

    ChannelType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 渠道代码
     * @return 渠道枚举
     */
    public static ChannelType fromCode(String code) {
        for (ChannelType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown channel type: " + code);
    }
}
