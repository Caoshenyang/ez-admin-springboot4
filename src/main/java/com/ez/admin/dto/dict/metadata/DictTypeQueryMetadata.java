package com.ez.admin.dto.dict.metadata;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.condition.FieldConfig;
import com.ez.admin.common.condition.QueryConditionSupport;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.common.metadata.FieldMetadata;
import com.ez.admin.common.metadata.QueryMetadata;
import com.ez.admin.modules.system.entity.SysDictType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 字典类型查询元数据枚举
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@AllArgsConstructor
public enum DictTypeQueryMetadata implements QueryMetadata<SysDictType> {

    // ========== 1. 枚举定义 ==========

    /** 字典名称 */
    DICT_NAME("dictName", "String", "字典名称", List.of(Operator.EQ, Operator.LIKE)) {
        @Override public SFunction<SysDictType, ?> getColumn() { return SysDictType::getDictName; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "用户状态"; }
    },

    /** 字典类型 */
    DICT_TYPE("dictType", "String", "字典类型", List.of(Operator.EQ, Operator.LIKE)) {
        @Override public SFunction<SysDictType, ?> getColumn() { return SysDictType::getDictType; }
        @Override public boolean isKeywordSearch() { return true; }
        @Override public Object getExample() { return "sys_user_status"; }
    },

    /** 状态（0=停用 1=正常） */
    STATUS("status", "Integer", "状态", List.of(Operator.EQ, Operator.IN)) {
        @Override public SFunction<SysDictType, ?> getColumn() { return SysDictType::getStatus; }
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
    public FieldConfig<SysDictType>[] toFieldConfigs() {
        return QueryMetadata.super.toFieldConfigs(values());
    }

    // ========== 4. 静态配置 ==========

    public static final FieldConfig<SysDictType>[] FIELD_CONFIGS;

    static {
        FIELD_CONFIGS = DICT_NAME.toFieldConfigs();
        QueryConditionSupport.register(SysDictType.class, FIELD_CONFIGS);
    }
}
