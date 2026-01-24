package com.ez.admin.common.model;

/**
 * 字段类型枚举
 *
 * @author ez-admin
 * @since 2026-01-24
 */
public enum FieldType {
    /**
     * 字符串类型（支持 EQ, NE, LIKE, NOT_LIKE, IN, NOT_IN）
     */
    STRING,

    /**
     * 整数类型（支持 EQ, NE, GT, GE, LT, LE, IN, NOT_IN）
     */
    INTEGER,

    /**
     * 长整类型（支持 EQ, NE, GT, GE, LT, LE, IN, NOT_IN）
     */
    LONG
}
