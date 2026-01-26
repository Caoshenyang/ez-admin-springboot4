package com.ez.admin.dto.role.metadata;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.condition.FieldConfig;
import com.ez.admin.common.condition.QueryConditionSupport;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.common.metadata.FieldMetadata;
import com.ez.admin.common.metadata.QueryMetadata;
import com.ez.admin.modules.system.entity.SysRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 角色查询元数据枚举
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@AllArgsConstructor
public enum RoleQueryMetadata implements QueryMetadata<SysRole> {

    // ========== 1. 枚举定义 ==========

    /** 角色名称 */
    ROLE_NAME("roleName", "String", "角色名称", List.of(Operator.EQ, Operator.LIKE)) {
        @Override public SFunction<SysRole, ?> getColumn() { return SysRole::getRoleName; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "管理员"; }
    },

    /** 角色权限字符标识 */
    ROLE_LABEL("roleLabel", "String", "角色权限字符", List.of(Operator.EQ, Operator.LIKE)) {
        @Override public SFunction<SysRole, ?> getColumn() { return SysRole::getRoleLabel; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "admin"; }
    },

    /** 数据范围（1=仅本人 2=本部门 3=本部门及以下 4=自定义 5=全部数据） */
    DATA_SCOPE("dataScope", "Integer", "数据范围", List.of(Operator.EQ, Operator.IN)) {
        @Override public SFunction<SysRole, ?> getColumn() { return SysRole::getDataScope; }
        @Override public boolean isKeywordSearch() { return false; }
        @Override public String getDictType() { return "sys_data_scope"; }
        @Override public Object getExample() { return 5; }
    },

    /** 角色状态（0=停用 1=正常） */
    STATUS("status", "Integer", "角色状态", List.of(Operator.EQ, Operator.IN)) {
        @Override public SFunction<SysRole, ?> getColumn() { return SysRole::getStatus; }
        @Override public boolean isKeywordSearch() { return false; }
        @Override public String getDictType() { return "sys_common_status"; }
        @Override public Object getExample() { return 1; }
    };

    // ========== 2. 字段定义 ==========

    private final String field;
    private final String type;
    private final String description;
    private final List<Operator> operators;

    // ========== 3. 接口实现 ==========

    @Override
    public List<FieldMetadata> getFields() {
        return Arrays.stream(values())
                .map(QueryMetadata::toFieldMetadata)
                .toList();
    }

    @Override
    public FieldConfig<SysRole>[] toFieldConfigs() {
        return QueryMetadata.super.toFieldConfigs(values());
    }

    // ========== 4. 静态配置 ==========

    public static final FieldConfig<SysRole>[] FIELD_CONFIGS;

    static {
        FIELD_CONFIGS = ROLE_NAME.toFieldConfigs();
        QueryConditionSupport.register(SysRole.class, FIELD_CONFIGS);
    }
}
