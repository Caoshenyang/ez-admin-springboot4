package com.ez.admin.common.condition;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.enums.FieldType;

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
 * </ul>
 *
 * @author ez-admin
 * @since 2026-01-24
 */
public record FieldConfig<T>(String fieldCode, SFunction<T, ?> column, FieldType type, boolean keywordSearch) {

    public static <T> FieldConfig<T> string(String fieldCode, SFunction<T, String> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING, false);
    }

    public static <T> FieldConfig<T> string(String fieldCode, SFunction<T, String> column, boolean keywordSearch) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING, keywordSearch);
    }

    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER, false);
    }

    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column, boolean keywordSearch) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER, keywordSearch);
    }

    public static <T> FieldConfig<T> longNum(String fieldCode, SFunction<T, Long> column) {
        return new FieldConfig<>(fieldCode, column, FieldType.LONG, false);
    }

    public static <T> FieldConfig<T> longNum(String fieldCode, SFunction<T, Long> column, boolean keywordSearch) {
        return new FieldConfig<>(fieldCode, column, FieldType.LONG, keywordSearch);
    }
}
