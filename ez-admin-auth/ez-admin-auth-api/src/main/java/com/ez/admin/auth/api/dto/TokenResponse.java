package com.ez.admin.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Token 响应 DTO
 * <p>
 * 登录成功后返回的 Token 信息
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌（Access Token）
     * <p>
     * 用于 API 访问认证，携带在请求头中：Authorization: Bearer {accessToken}
     * 有效期：15分钟
     * </p>
     */
    private String accessToken;

    /**
     * 访问令牌过期时间（Unix 时间戳，秒）
     */
    private Long accessTokenExpiresIn;

    /**
     * 刷新令牌（Refresh Token）
     * <p>
     * 用于刷新 Access Token，当 Access Token 过期时调用刷新接口
     * 有效期：7天
     * </p>
     */
    private String refreshToken;

    /**
     * 刷新令牌过期时间（Unix 时间戳，秒）
     */
    private Long refreshTokenExpiresIn;

    /**
     * 设备令牌（Device Token，可选）
     * <p>
     * 用于"记住登录"功能，当用户勾选"记住登录"时返回
     * 有效期：30天
     * </p>
     */
    private String deviceToken;

    /**
     * 设备令牌过期时间（Unix 时间戳，秒）
     */
    private Long deviceTokenExpiresIn;

    /**
     * Token 类型（固定值：Bearer）
     */
    private String tokenType = "Bearer";

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 是否首次登录
     * <p>
     * true: 首次登录，前端可能需要引导用户完善信息
     * false: 非首次登录
     * </p>
     */
    private Boolean isFirstLogin = false;
}
