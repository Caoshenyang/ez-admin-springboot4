package com.ez.admin.common.core.constant;

/**
 * Redis Key 常量
 * <p>
 * 统一管理 Redis 中使用的 Key 前缀，避免 Key 冲突
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
public final class RedisKeyConstants {

    /**
     * 用户角色信息 Key 前缀
     * <p>
     * 完整 Key 格式：user:roles:{userId}
     * 存储内容：List&lt;RoleInfo&gt; 角色信息列表（包含 roleId、roleLabel、roleName）
     * </p>
     */
    public static final String USER_ROLES_KEY_PREFIX = "user:roles:";

    private RedisKeyConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
