package com.ez.admin.common.model;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * 实体字段配置
 * <p>
 * 用于定义哪些字段可以参与动态过滤查询
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-24
 */
public record FieldConfig<T>(
        /**
         * 字段代码（前端传入的标识，建议使用驼峰命名，如 username, status）
         */
        String fieldCode,

        /**
         * Lambda 字段引用（类型安全）
         */
        SFunction<T, ?> column,

        /**
         * 字段类型
         */
        FieldType type
) {
    /**
     * 创建字符串字段配置
     */
    public static <T> FieldConfig<T> string(String fieldCode, SFunction<T, String> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING);
    }

    /**
     * 创建整数字段配置
     */
    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER);
    }

    /**
     * 创建长整字段配置
     */
    public static <T> FieldConfig<T> longNum(String fieldCode, SFunction<T, Long> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.LONG);
    }
}
