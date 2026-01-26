package com.ez.admin.common.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sa-Token 权限校验注解
 * <p>
 * 用于标注需要权限校验的接口，只有拥有对应权限的用户才能访问
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * @SaCheckPermission("system:user:create")
 * @PostMapping
 * public R<Void> create(@RequestBody UserCreateReq request) {
 *     // ...
 * }
 * }</pre>
 * </p>
 * <p>
 * 权限字符串格式：`模块:功能:操作`
 * <ul>
 *   <li>system:user:create - 系统管理-用户管理-创建用户</li>
 *   <li>system:user:update - 系统管理-用户管理-更新用户</li>
 *   <li>system:user:delete - 系统管理-用户管理-删除用户</li>
 *   <li>system:user:query - 系统管理-用户管理-查询用户</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SaCheckPermission {

    /**
     * 需要校验的权限码
     */
    String value();

    /**
     * 是否需要登录才能访问（默认 true）
     * <p>
     * 设置为 false 时，即使未登录也可以访问（用于注册、登录等接口）
     * </p>
     */
    boolean login() default true;

    /**
     * 验证模式：AND 或 OR
     * <p>
     * 如果设置了多个权限码，通过此属性指定是满足所有权限（AND）还是满足任一权限（OR）
     * </p>
     */
    SaMode mode() default SaMode.AND;

    /**
     * 验证模式枚举
     */
    enum SaMode {
        /**
         * 必须具有所有权限
         */
        AND,

        /**
         * 只需具有其中一个权限
         */
        OR
    }
}
