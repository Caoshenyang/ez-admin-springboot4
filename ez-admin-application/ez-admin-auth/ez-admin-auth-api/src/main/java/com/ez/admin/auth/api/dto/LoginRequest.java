package com.ez.admin.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 登录请求 DTO
 * <p>
 * 统一的登录请求对象，支持多渠道登录
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Data
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录渠道（必填）
     */
    @NotBlank(message = "登录渠道不能为空")
    private String channelType;

    /**
     * 设备 ID（必填）
     * <p>
     * 用于设备管理和多设备登录控制
     * </p>
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 设备名称（可选）
     * <p>
     * 用于在设备管理界面展示，如 "iPhone 14 Pro"、"Windows PC"
     * </p>
     */
    private String deviceName;

    /**
     * 登录凭证（动态字段）
     * <p>
     * 不同渠道的凭证不同：
     * - 微信小程序：code（微信授权码）
     * - 账号密码：username + password
     * - 手机验证码：phone + smsCode
     * </p>
     */
    private Map<String, String> credentials;

    /**
     * 扩展参数（可选）
     * <p>
     * 用于携带额外的认证信息，如：
     * - ipAddress: 客户端 IP 地址
     * - userAgent: 客户端 User-Agent
     * - captcha: 图形验证码
     * </p>
     */
    private Map<String, Object> extra;

    /**
     * 是否记住登录（可选）
     * <p>
     * true: 发放 Device Token，有效期 30 天
     * false: 不发放 Device Token，仅发放 Access Token + Refresh Token
     * </p>
     */
    private Boolean rememberMe = false;

    /**
     * 获取凭证值
     *
     * @param key 凭证键
     * @return 凭证值
     */
    public String getCredential(String key) {
        return credentials != null ? credentials.get(key) : null;
    }

    /**
     * 获取扩展参数值
     *
     * @param key 参数键
     * @return 参数值
     */
    public Object getExtraParam(String key) {
        return extra != null ? extra.get(key) : null;
    }
}
