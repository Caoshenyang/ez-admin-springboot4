package com.ez.admin.common.core.constant;

/**
 * 系统通用常量
 * <p>
 * 包含系统中使用的各类常量定义，避免魔法值的使用
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
public final class SystemConstants {

    /**
     * 删除标记
     * 0=正常（未删除），1=已删除
     */
    public static final int NOT_DELETED = 0;
    public static final int DELETED = 1;

    /**
     * 性别
     * 0=保密，1=男，2=女
     */
    public static final int GENDER_SECRET = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    /**
     * 状态
     * 0=禁用，1=正常
     */
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_NORMAL = 1;

    /**
     * 角色数据范围
     * 1=仅本人数据权限，2=本部门数据权限，3=本部门及以下数据权限，4=自定义数据权限，5=全部数据权限
     */
    public static final int DATA_SCOPE_SELF = 1;
    public static final int DATA_SCOPE_DEPT = 2;
    public static final int DATA_SCOPE_DEPT_AND_CHILD = 3;
    public static final int DATA_SCOPE_CUSTOM = 4;
    public static final int DATA_SCOPE_ALL = 5;

    /**
     * 系统标识
     */
    public static final String CREATOR_SYSTEM = "system";
    public static final String DEFAULT_DESCRIPTION = "系统默认管理员账号";

    /**
     * 超级管理员角色
     */
    public static final String ROLE_NAME_SUPER_ADMIN = "超级管理员";
    public static final String ROLE_LABEL_SUPER_ADMIN = "SUPER_ADMIN";
    public static final int ROLE_SORT_SUPER_ADMIN = 1;
    public static final String ROLE_DESC_SUPER_ADMIN = "系统超级管理员角色，拥有所有权限";

    private SystemConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
