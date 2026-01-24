package com.ez.admin.dto.common;

import lombok.Getter;

/**
 * 比较操作符枚举
 * <p>
 * 定义支持的查询操作符类型，配合 FilterSupport 使用
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
public enum Operator {

    /**
     * 等于
     */
    EQ("EQ", "等于"),

    /**
     * 不等于
     */
    NE("NE", "不等于"),

    /**
     * 大于
     */
    GT("GT", "大于"),

    /**
     * 大于等于
     */
    GE("GE", "大于等于"),

    /**
     * 小于
     */
    LT("LT", "小于"),

    /**
     * 小于等于
     */
    LE("LE", "小于等于"),

    /**
     * 模糊匹配
     */
    LIKE("LIKE", "模糊匹配"),

    /**
     * 不包含
     */
    NOT_LIKE("NOT_LIKE", "不包含"),

    /**
     * 包含于（逗号分隔）
     */
    IN("IN", "包含于"),

    /**
     * 不包含于（逗号分隔）
     */
    NOT_IN("NOT_IN", "不包含于");

    /**
     * 操作符代码
     */
    private final String code;

    /**
     * 操作符名称
     */
    private final String name;

    Operator(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据操作符代码获取枚举值
     *
     * @param code 操作符代码
     * @return Operator 枚举值，未找到返回 null
     */
    public static Operator fromOperator(String code) {
        if (code == null) {
            return null;
        }
        for (Operator op : values()) {
            if (op.code.equals(code)) {
                return op;
            }
        }
        return null;
    }
}
