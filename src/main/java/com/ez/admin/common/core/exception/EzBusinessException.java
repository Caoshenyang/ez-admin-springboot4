package com.ez.admin.common.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常类
 * <p>
 * 用于封装业务处理过程中的错误信息，所有业务异常的基类。
 * 支持使用错误码枚举和自定义错误消息两种方式构建异常。
 * </p>
 * <p>
 * 使用示例（基于 5 位数字错误码标准）：
 * <pre>{@code
 * // 使用错误码枚举
 * throw new EzBusinessException(ErrorCode.USER_NOT_FOUND); // 20101
 *
 * // 使用错误码枚举 + 自定义消息
 * throw new EzBusinessException(ErrorCode.USER_PASSWORD_ERROR, "用户ID: " + userId); // 20102
 *
 * // 仅使用自定义消息（默认使用 10500 错误码）
 * throw new EzBusinessException("数据保存失败"); // 10500
 * }</pre>
 * </p>
 *
 * @see ErrorCode 错误码枚举（5位数字分段式设计）
 */
@Getter
@Setter
public class EzBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 使用错误码枚举构建异常
     *
     * @param errorCode 错误码枚举
     */
    public EzBusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 使用错误码枚举和自定义消息构建异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息（会覆盖枚举中的默认消息）
     */
    public EzBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.message = message;
    }

    /**
     * 使用错误码枚举、自定义消息和原始异常构建异常
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param cause     原始异常（用于异常链追踪）
     */
    public EzBusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.message = message;
    }

    /**
     * 使用错误码枚举和原始异常构建异常
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常（用于异常链追踪）
     */
    public EzBusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 使用自定义消息构建异常（默认使用 10500 错误码）
     *
     * @param message 自定义错误消息
     */
    public EzBusinessException(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    /**
     * 使用自定义消息和原始异常构建异常（默认使用 10500 错误码）
     *
     * @param message 自定义错误消息
     * @param cause   原始异常（用于异常链追踪）
     */
    public EzBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    /**
     * 使用自定义错误码和消息构建异常
     *
     * @param code    自定义错误码
     * @param message 自定义错误消息
     */
    public EzBusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 使用自定义错误码、消息和原始异常构建异常
     *
     * @param code    自定义错误码
     * @param message 自定义错误消息
     * @param cause   原始异常（用于异常链追踪）
     */
    public EzBusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
