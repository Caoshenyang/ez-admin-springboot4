package com.ez.admin.common.condition;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.enums.FieldType;
import com.ez.admin.common.enums.Operator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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
@Getter
@AllArgsConstructor
public class FieldConfig<T> {

    /**
     * 字段代码（前端传入，驼峰命名如 username）
     */
    private final String fieldCode;

    /**
     * Lambda 字段引用（类型安全）
     */
    private final SFunction<T, ?> column;

    /**
     * 字段类型
     */
    private final FieldType type;

    /**
     * 是否用于快捷搜索
     */
    private final boolean keywordSearch;

    /**
     * 支持的操作符列表
     */
    private final Operator[] operators;

    /**
     * 字典类型（可选）
     */
    private final String dictType;

    /**
     * 字段描述
     */
    private final String description;

    /**
     * 创建字符串字段配置（快捷搜索）
     */
    public static <T> FieldConfig<T> searchableString(String fieldCode, SFunction<T, String> column, String description) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING, true,
                new Operator[]{Operator.EQ, Operator.LIKE}, null, description);
    }

    /**
     * 创建字符串字段配置
     */
    public static <T> FieldConfig<T> string(String fieldCode, SFunction<T, String> column, String description) {
        return new FieldConfig<>(fieldCode, column, FieldType.STRING, false,
                new Operator[]{Operator.EQ, Operator.LIKE}, null, description);
    }

    /**
     * 创建整数字段配置（带字典）
     */
    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column, String dictType, String description) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER, false,
                new Operator[]{Operator.EQ, Operator.IN}, dictType, description);
    }

    /**
     * 创建整数字段配置
     */
    public static <T> FieldConfig<T> integer(String fieldCode, SFunction<T, Integer> column, String description) {
        return new FieldConfig<>(fieldCode, column, FieldType.INTEGER, false,
                new Operator[]{Operator.EQ, Operator.IN}, null, description);
    }

    /**
     * 创建长整字段配置
     */
    public static <T> FieldConfig<T> longNum(String fieldCode, SFunction<T, Long> column, String description) {
        return new FieldConfig<>(fieldCode, column, FieldType.LONG, false,
                new Operator[]{Operator.EQ, Operator.IN}, null, description);
    }

    @Override
    public String toString() {
        return "FieldConfig{" +
                "fieldCode='" + fieldCode + '\'' +
                ", type=" + type +
                ", keywordSearch=" + keywordSearch +
                ", operators=" + Arrays.toString(operators) +
                '}';
    }
}
