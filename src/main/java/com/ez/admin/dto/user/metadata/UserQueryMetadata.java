package com.ez.admin.dto.user.metadata;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.condition.FieldConfig;
import com.ez.admin.common.condition.QueryConditionSupport;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.common.metadata.FieldMetadata;
import com.ez.admin.common.metadata.QueryMetadata;
import com.ez.admin.modules.system.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 用户查询元数据枚举
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@AllArgsConstructor
public enum UserQueryMetadata implements QueryMetadata<SysUser> {

    // ========== 1. 枚举定义（最前面，Java 语法要求） ==========

    /** 用户账号 */
    USERNAME("username", "String", "用户账号", List.of(Operator.EQ, Operator.LIKE, Operator.IN)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getUsername; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "admin"; }
    },

    /** 用户昵称 */
    NICKNAME("nickname", "String", "用户昵称", List.of(Operator.LIKE)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getNickname; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "张三"; }
    },

    /** 用户邮箱 */
    EMAIL("email", "String", "用户邮箱", List.of(Operator.EQ, Operator.LIKE)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getEmail; }
        @Override public boolean isKeywordSearch() { return false; }
        @Override public Object getExample() { return "admin@example.com"; }
    },

    /** 用户手机号码 */
    PHONE_NUMBER("phoneNumber", "String", "手机号码", List.of(Operator.EQ, Operator.LIKE)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getPhoneNumber; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "13800138000"; }
    },

    /** 性别（字典：sys_gender，0=保密, 1=男, 2=女） */
    GENDER("gender", "Integer", "性别", List.of(Operator.EQ, Operator.IN)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getGender; }
        @Override public boolean isKeywordSearch() { return false; }
        @Override public String getDictType() { return "sys_gender"; }
        @Override public Object getExample() { return 1; }
    },

    /** 用户状态（字典：sys_user_status，0=禁用, 1=正常） */
    STATUS("status", "Integer", "用户状态", List.of(Operator.EQ, Operator.IN)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getStatus; }
        @Override public boolean isKeywordSearch() { return false; }
        @Override public String getDictType() { return "sys_user_status"; }
        @Override public Object getExample() { return 1; }
    },

    /** 部门ID */
    DEPT_ID("deptId", "Long", "部门ID", List.of(Operator.EQ, Operator.IN)) {
        @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getDeptId; }
        @Override public boolean isKeywordSearch() { return false; }
        @Override public Object getExample() { return 1L; }
    };

    // ========== 2. 字段定义（紧跟在枚举定义之后） ==========

    private final String field;              // 数据库字段名
    private final String type;               // Java 类型
    private final String description;        // 中文描述
    private final List<Operator> operators;  // 支持的操作符列表

    // ========== 3. 接口实现 ==========

    /**
     * 获取所有字段的元数据列表
     */
    @Override
    public List<FieldMetadata> getFields() {
        return Arrays.stream(values())
                .map(QueryMetadata::toFieldMetadata)
                .toList();
    }

    /**
     * 生成所有字段的 FieldConfig 数组
     */
    @Override
    public FieldConfig<SysUser>[] toFieldConfigs() {
        return QueryMetadata.super.toFieldConfigs(values());
    }

    // ========== 4. 静态配置（最后面） ==========

    /**
     * 所有字段的 FieldConfig 数组（静态常量）
     */
    public static final FieldConfig<SysUser>[] FIELD_CONFIGS;

    static {
        FIELD_CONFIGS = USERNAME.toFieldConfigs();
        QueryConditionSupport.register(SysUser.class, FIELD_CONFIGS);
    }
}
