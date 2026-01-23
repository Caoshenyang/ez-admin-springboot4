package com.ez.admin.dto.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 比较操作符枚举
 * <p>
 * 定义支持的查询操作符类型
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@RequiredArgsConstructor
public enum Operator {

    /**
     * 等于
     */
    EQ("等于", (wrapper, column, value) -> wrapper.eq(column, parseValue(value))),

    /**
     * 不等于
     */
    NE("不等于", (wrapper, column, value) -> wrapper.ne(column, parseValue(value))),

    /**
     * 大于
     */
    GT("大于", (wrapper, column, value) -> wrapper.gt(column, parseComparable(value))),

    /**
     * 大于等于
     */
    GE("大于等于", (wrapper, column, value) -> wrapper.ge(column, parseComparable(value))),

    /**
     * 小于
     */
    LT("小于", (wrapper, column, value) -> wrapper.lt(column, parseComparable(value))),

    /**
     * 小于等于
     */
    LE("小于等于", (wrapper, column, value) -> wrapper.le(column, parseComparable(value))),

    /**
     * 模糊匹配
     */
    LIKE("模糊匹配", (wrapper, column, value) -> wrapper.like(column, String.valueOf(value))),

    /**
     * 不包含
     */
    NOT_LIKE("不包含", (wrapper, column, value) -> wrapper.notLike(column, String.valueOf(value))),

    /**
     * 包含于（逗号分隔）
     */
    IN("包含于", (wrapper, column, value) -> {
        String[] values = String.valueOf(value).split(",");
        wrapper.in(column, (Object[]) values);
    }),

    /**
     * 不包含于（逗号分隔）
     */
    NOT_IN("不包含于", (wrapper, column, value) -> {
        String[] values = String.valueOf(value).split(",");
        wrapper.notIn(column, (Object[]) values);
    });

    private final String name;

    /**
     * 条件应用器
     */
    private final TriFunction<LambdaQueryWrapper<?>, Object, Object, LambdaQueryWrapper<?>> applicator;

    /**
     * 应用查询条件
     *
     * @param wrapper LambdaQueryWrapper
     * @param column  字段 Lambda
     * @param value   字段值
     * @return LambdaQueryWrapper
     */
    @SuppressWarnings("unchecked")
    public <T> LambdaQueryWrapper<T> apply(LambdaQueryWrapper<T> wrapper, Object column, Object value) {
        return (LambdaQueryWrapper<T>) applicator.apply(wrapper, column, value);
    }

    /**
     * 根据操作符名称获取枚举值
     *
     * @param operator 操作符名称
     * @return Operator 枚举值
     */
    public static Operator fromOperator(String operator) {
        for (Operator op : values()) {
            if (op.name().equals(operator)) {
                return op;
            }
        }
        return null;
    }

    /**
     * 解析为可比较的值
     */
    private static Comparable<?> parseComparable(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 尝试解析为时间
            return value; // 实际使用时可以解析为 LocalDateTime
        }
    }

    /**
     * 解析为普通值
     */
    private static Object parseValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return value;
        }
        return String.valueOf(value);
    }

    /**
     * 三参数函数式接口
     */
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
}
