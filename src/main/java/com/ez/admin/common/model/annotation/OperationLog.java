package com.ez.admin.common.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * <p>
 * 用于标注需要记录操作日志的方法，AOP 切面会自动拦截并记录日志
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 模块名称
     * <p>例如：用户管理、角色管理</p>
     */
    String module() default "";

    /**
     * 操作类型
     * <p>例如：创建、更新、删除、查询</p>
     */
    String operation() default "";

    /**
     * 操作描述
     * <p>详细描述本次操作，例如：创建用户、删除角色</p>
     */
    String description() default "";
}
