package com.ez.admin.common.condition;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.enums.FieldType;
import com.ez.admin.common.enums.Operator;

/**
 * 实体字段配置
 * <p>
 * 用于定义哪些字段可以参与动态查询
 * </p>
 * <p>
 * 组件说明：
 * <ul>
 *   <li>fieldCode: 字段代码（前端传入，驼峰命名如 username）</li>
 *   <li>column: Lambda 字段引用（类型安全）</li>
 *   <li>type: 字段类型（STRING/INTEGER/LONG）</li>
 *   <li>keywordSearch: 是否用于快捷搜索（默认 false）</li>
 *   <li>operators: 支持的操作符列表</li>
 *   <li>dictType: 字典类型（可选）</li>
 * </ul>
 *
 * @author ez-admin
 * @since 2026-01-24
 */
public record FieldConfig<T>(
        String fieldCode,
        SFunction<T, ?> column,
        FieldType type,
        boolean keywordSearch,
        Operator[] operators,
        String dictType
) {

    /**
     * 创建字符串字段配置（快捷搜索）
     */
    public static <T> FieldConfig<T> searchableString(String fieldCode, SFunction<T, String> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING, true,
                new Operator[]{Operator.EQ, Operator.LIKE}, null);
    }

    /**
     * 创建字符串字段配置
     */
    public static <T> FieldConfig<T> string(String fieldCode, SFunction<T, String> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING, false,
                new Operator[]{Operator.EQ, Operator.LIKE}, null);
    }

    /**
     * 创建整数字段配置（带字典）
     */
    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column, String dictType) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER, false,
                new Operator[]{Operator.EQ, Operator.IN}, dictType);
    }

    /**
     * 创建整数字段配置
     */
    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER, false,
                new Operator[]{Operator.EQ, Operator.IN}, null);
    }

    /**
     * 创建长整字段配置
     */
    public static <T> FieldConfig<T> longNum(String fieldCode, SFunction<T, Long> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.LONG, false,
                new Operator[]{Operator.EQ, Operator.IN}, null);
    }
}
