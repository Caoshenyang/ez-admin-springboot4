package com.ez.admin.auth.api.exception;

import lombok.Getter;

/**
 * 认证异常
 * <p>
 * 统一的认证异常类，用于封装认证过程中的各类错误
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Getter
public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private final String errorCode;

    /**
     * 错误消息
     */
    private final String errorMessage;

    /**
     * 构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public AuthenticationException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 构造函数（带原始异常）
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @param cause        原始异常
     */
    public AuthenticationException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 预定义的错误代码
     */
    public static class ErrorCodes {
        /** 无效的登录渠道 */
        public static final String INVALID_CHANNEL = "AUTH_001";

        /** 登录凭证无效 */
        public static final String INVALID_CREDENTIALS = "AUTH_002";

        /** 用户不存在 */
        public static final String USER_NOT_FOUND = "AUTH_003";

        /** 用户已禁用 */
        public static final String USER_DISABLED = "AUTH_004";

        /** Token 无效 */
        public static final String INVALID_TOKEN = "AUTH_005";

        /** Token 已过期 */
        public static final String TOKEN_EXPIRED = "AUTH_006";

        /** 设备未授权 */
        public static final String DEVICE_NOT_AUTHORIZED = "AUTH_007";

        /** 刷新 Token 失效 */
        public static final String REFRESH_TOKEN_INVALID = "AUTH_008";

        /** 设备数量超限 */
        public static final String DEVICE_LIMIT_EXCEEDED = "AUTH_009";

        /** 验证码错误 */
        public static final String CAPTCHA_ERROR = "AUTH_010";

        /** 短信验证码错误 */
        public static final String SMS_CODE_ERROR = "AUTH_011";

        /** 微信授权码无效 */
        public static final String WECHAT_CODE_INVALID = "AUTH_012";
    }
}
