package com.ez.admin.common.enums;

import lombok.Getter;

/**
 * 查询操作符枚举
 * <p>
 * 支持：EQ(等于), NE(不等于), GT/GE(大于/大于等于), LT/LE(小于/小于等于),
 * LIKE/NOT_LIKE(模糊匹配), IN/NOT_IN(包含/不包含，逗号分隔)
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
public enum Operator {

    EQ("EQ", "等于"),
    NE("NE", "不等于"),
    GT("GT", "大于"),
    GE("GE", "大于等于"),
    LT("LT", "小于"),
    LE("LE", "小于等于"),
    LIKE("LIKE", "模糊匹配"),
    NOT_LIKE("NOT_LIKE", "不包含"),
    IN("IN", "包含于"),
    NOT_IN("NOT_IN", "不包含于");

    private final String code;
    private final String name;

    Operator(String code, String name) {
        this.code = code;
        this.name = name;
    }

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
