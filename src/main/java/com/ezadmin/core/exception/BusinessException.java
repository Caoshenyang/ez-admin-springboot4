package com.ezadmin.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-15 10:49:35
 */
@Setter
@Getter
public class BusinessException extends RuntimeException{

    /**
     * 错误代码
     */
    private int code;

    /**
     * 构造方法
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500; // 默认错误代码
    }

    /**
     * 构造方法
     * @param code 错误代码
     * @param message 错误消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造方法
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500; // 默认错误代码
    }

    /**
     * 构造方法
     * @param code 错误代码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

}
