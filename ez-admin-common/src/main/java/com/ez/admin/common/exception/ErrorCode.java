package com.ez.admin.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误码枚举
 * <p>
 * 定义系统中通用的业务错误码，用于标准化错误处理和国际化支持。
 * 每个错误码包含数值编码和默认错误描述。
 * </p>
 *
 * <p>错误码分类约定：</p>
 * <ul>
 *   <li>1xx: 信息提示</li>
 *   <li>2xx: 成功</li>
 *   <li>4xx: 客户端错误（参数、权限等）</li>
 *   <li>5xx: 服务端错误（系统异常、业务异常等）</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ========== 成功 ==========
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问该资源"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突"),

    // ========== 参数校验错误 ==========
    VALIDATION_ERROR(4001, "参数校验失败"),
    MISSING_REQUIRED_PARAMETER(4002, "缺少必填参数"),
    INVALID_PARAMETER_FORMAT(4003, "参数格式错误"),
    PARAMETER_OUT_OF_RANGE(4004, "参数超出允许范围"),

    // ========== 业务错误 5xx ==========
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    DATABASE_ERROR(5001, "数据库操作失败"),
    BUSINESS_ERROR(5002, "业务处理失败"),

    // ========== 用户相关错误 ==========
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    USER_PASSWORD_ERROR(10003, "用户名或密码错误"),
    USER_ACCOUNT_DISABLED(10004, "用户账号已被禁用"),
    USER_ACCOUNT_LOCKED(10005, "用户账号已被锁定"),

    // ========== 角色权限相关错误 ==========
    ROLE_NOT_FOUND(20001, "角色不存在"),
    ROLE_ALREADY_EXISTS(20002, "角色已存在"),
    PERMISSION_NOT_FOUND(20003, "权限不存在"),
    ROLE_ASSIGNED_TO_USER(20004, "该角色已分配给用户，无法删除"),

    // ========== 菜单相关错误 ==========
    MENU_NOT_FOUND(30001, "菜单不存在"),
    MENU_ALREADY_EXISTS(30002, "菜单已存在"),
    MENU_HAS_CHILDREN(30003, "菜单存在子菜单，无法删除"),
    MENU_PARENT_NOT_FOUND(30004, "父菜单不存在"),

    // ========== 部门相关错误 ==========
    DEPARTMENT_NOT_FOUND(40001, "部门不存在"),
    DEPARTMENT_ALREADY_EXISTS(40002, "部门已存在"),
    DEPARTMENT_HAS_USERS(40003, "部门下存在用户，无法删除"),
    DEPARTMENT_HAS_CHILDREN(40004, "部门存在子部门，无法删除"),

    // ========== 字典相关错误 ==========
    DICT_TYPE_NOT_FOUND(50001, "字典类型不存在"),
    DICT_TYPE_ALREADY_EXISTS(50002, "字典类型已存在"),
    DICT_DATA_NOT_FOUND(50003, "字典数据不存在"),
    DICT_DATA_ALREADY_EXISTS(50004, "字典数据已存在"),

    // ========== 文件相关错误 ==========
    FILE_UPLOAD_ERROR(60001, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(60002, "文件下载失败"),
    FILE_NOT_FOUND(60003, "文件不存在"),
    FILE_SIZE_EXCEEDED(60004, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(60005, "不支持的文件类型"),

    // ========== 第三方服务错误 ==========
    THIRD_PARTY_SERVICE_ERROR(70001, "第三方服务调用失败"),
    SMS_SEND_ERROR(70002, "短信发送失败"),
    EMAIL_SEND_ERROR(70003, "邮件发送失败");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误描述
     */
    private final String message;

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     * @return 对应的错误码枚举，未找到时返回 INTERNAL_SERVER_ERROR
     */
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}
