package com.ez.admin.common.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误码枚举（5位数字分段式设计）
 * <p>
 * 采用 ABBCC 格式：
 * <ul>
 *   <li>A (1位): 服务/大类级（0=成功, 1=系统级, 2=业务级, 3=三方服务）</li>
 *   <li>BB (2位): 模块级（01=通用, 01=用户, 02=角色, 03=菜单, 04=部门, 05=字典, 06=认证, 07=文件）</li>
 *   <li>CC (2位): 具体错误流水号</li>
 * </ul>
 * </p>
 *
 * <p>错误码分类：</p>
 * <ul>
 *   <li>0xxxx: 成功</li>
 *   <li>1xxxx: 系统级错误（参数、权限、限流等通用错误）</li>
 *   <li>2xxxx: 业务级错误（逻辑层面的业务错误）</li>
 *   <li>3xxxx: 三方服务错误（数据库、Redis、短信平台等）</li>
 * </ul>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ================================ 0xxxx: 成功 ================================

    SUCCESS(0, "操作成功"),

    // ================================ 1xxxx: 系统级错误 ================================

    BAD_REQUEST(10001, "请求参数格式错误"),
    UNAUTHORIZED(10002, "未授权，请先登录"),
    FORBIDDEN(10003, "权限不足，无权访问"),
    TOO_MANY_REQUESTS(10004, "请求过于频繁，请稍后再试"),
    VALIDATION_ERROR(10005, "参数校验失败"),
    MISSING_PARAMETER(10006, "缺少必填参数"),
    INVALID_PARAMETER_FORMAT(10007, "参数格式错误"),
    PARAMETER_OUT_OF_RANGE(10008, "参数超出允许范围"),
    NOT_FOUND(10009, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(10010, "请求方法不支持"),
    INTERNAL_SERVER_ERROR(10500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(10503, "服务暂时不可用"),

    // ================================ 2xxxx: 业务级错误 ================================

    // ========== 201xx: 用户模块 ==========
    USER_NOT_FOUND(20101, "用户不存在"),
    USER_PASSWORD_ERROR(20102, "用户名或密码错误"),
    USER_ACCOUNT_DISABLED(20103, "账号已被禁用"),
    USER_ACCOUNT_LOCKED(20104, "账号已被锁定"),
    USER_ALREADY_EXISTS(20105, "用户已存在"),
    USER_PHONE_ALREADY_EXISTS(20106, "手机号已存在"),
    USER_EMAIL_ALREADY_EXISTS(20107, "邮箱已存在"),

    // ========== 202xx: 角色模块 ==========
    ROLE_NOT_FOUND(20201, "角色不存在"),
    ROLE_ALREADY_EXISTS(20202, "角色已存在"),
    ROLE_ASSIGNED_TO_USER(20203, "角色已分配给用户，无法删除"),
    PERMISSION_NOT_FOUND(20204, "权限不存在"),

    // ========== 203xx: 菜单模块 ==========
    MENU_NOT_FOUND(20301, "菜单不存在"),
    MENU_ALREADY_EXISTS(20302, "菜单已存在"),
    MENU_HAS_CHILDREN(20303, "菜单存在子菜单，无法删除"),
    MENU_PARENT_NOT_FOUND(20304, "父菜单不存在"),

    // ========== 204xx: 部门模块 ==========
    DEPT_NOT_FOUND(20401, "部门不存在"),
    DEPT_ALREADY_EXISTS(20402, "部门已存在"),
    DEPT_HAS_USERS(20403, "部门下存在用户，无法删除"),
    DEPT_HAS_CHILDREN(20404, "部门存在子部门，无法删除"),

    // ========== 205xx: 字典模块 ==========
    DICT_TYPE_NOT_FOUND(20501, "字典类型不存在"),
    DICT_TYPE_ALREADY_EXISTS(20502, "字典类型已存在"),
    DICT_DATA_NOT_FOUND(20503, "字典数据不存在"),
    DICT_DATA_ALREADY_EXISTS(20504, "字典数据已存在"),

    // ========== 206xx: 认证模块 ==========
    TOKEN_INVALID(20601, "Token 无效或已过期"),
    REFRESH_TOKEN_INVALID(20602, "Refresh Token 无效或已过期"),
    DEVICE_NOT_AUTHORIZED(20603, "设备未授权"),
    DEVICE_LOGIN_ELSEWHERE(20604, "设备已在其他地方登录"),
    VERIFICATION_CODE_ERROR(20605, "验证码错误或已过期"),
    VERIFICATION_CODE_TOO_FREQUENT(20606, "验证码发送频繁，请稍后再试"),

    // ========== 207xx: 文件模块 ==========
    FILE_UPLOAD_ERROR(20701, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(20702, "文件下载失败"),
    FILE_NOT_FOUND(20703, "文件不存在"),
    FILE_SIZE_EXCEEDED(20704, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(20705, "不支持的文件类型"),

    // ================================ 3xxxx: 三方服务错误 ================================

    // ========== 301xx: 数据库服务 ==========
    DATABASE_ERROR(30101, "数据库操作失败"),
    DATABASE_TIMEOUT(30102, "数据库连接超时"),
    DUPLICATE_RECORD(30103, "数据记录已存在"),

    // ========== 302xx: Redis 服务 ==========
    REDIS_ERROR(30201, "Redis 操作失败"),
    REDIS_TIMEOUT(30202, "Redis 连接超时"),

    // ========== 303xx: 短信服务 ==========
    SMS_SEND_ERROR(30301, "短信发送失败"),
    SMS_TEMPLATE_NOT_FOUND(30302, "短信模板不存在"),
    SMS_BALANCE_NOT_ENOUGH(30303, "短信余额不足"),

    // ========== 304xx: 邮件服务 ==========
    EMAIL_SEND_ERROR(30401, "邮件发送失败"),

    // ========== 305xx: 第三方服务通用 ==========
    THIRD_PARTY_SERVICE_ERROR(30501, "第三方服务调用失败"),
    THIRD_PARTY_SERVICE_TIMEOUT(30502, "第三方服务超时"),

    // ========== 208xx: 初始化模块 ==========
    SYSTEM_ALREADY_INITIALIZED(20801, "系统已初始化，无法重复执行初始化"),
    SYSTEM_INIT_FAILED(20802, "系统初始化失败"),
    SYSTEM_NOT_INITIALIZED(20803, "系统尚未初始化，请先初始化系统");

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

    /**
     * 判断是否为成功响应
     *
     * @return true=成功, false=失败
     */
    public boolean isSuccess() {
        return this.code == 0;
    }
}
