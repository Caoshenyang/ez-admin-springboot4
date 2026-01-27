package com.ez.admin.common.constant;

/**
 * 角色标识常量
 * <p>
 * 定义系统中所有角色的标识码，用于权限判断和角色识别
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
public interface RoleCode {

    /**
     * 超级管理员角色标识
     * <p>
     * 拥有所有权限，自动获得所有菜单权限
     * </p>
     */
    String SUPER_ADMIN = "SUPER_ADMIN";

    /**
     * 普通管理员角色标识（示例）
     */
    String ADMIN = "ADMIN";

    /**
     * 普通用户角色标识（示例）
     */
    String USER = "USER";
}
