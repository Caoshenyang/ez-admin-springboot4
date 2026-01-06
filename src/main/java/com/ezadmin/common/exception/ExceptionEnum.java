package com.ezadmin.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类名: ExceptionEnum
 * 功能描述: 异常枚举
 *
 * @author shenyang
 * @since 2025/3/14 16:23
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    /**
     * ezadmin 系统业务异常响应状态码  5xxx1 - 59999
     */
    ADMIN_ACCOUNT_INITIALIZED(50001, "管理员账号已初始化,请勿重复操作"),
    USERNAME_NOT_EXISTS(50002, "用户名不存在"),
    USERNAME_EXISTS(50003, "用户名已存在"),
    USERNAME_OR_PASSWORD_ERROR(50004, "用户名或密码错误"),

    USER_NOT_LOGGED_IN(50005, "用户未登录"),
    TOKEN_EXPIRED(50006, "登录已过期，请重新登录"),
    TOKEN_INVALID(50007, "登录状态无效，请重新登录"),
    TOKEN_FROZEN(50008, "账号已被冻结"),
    LOGIN_STATUS_ERROR(50009, "账号已在其他设备登录"),
    ;
    /**
     * 执行状态码
     */
    private final int code;
    /**
     * 执行状态描述
     */
    private final String message;
}
