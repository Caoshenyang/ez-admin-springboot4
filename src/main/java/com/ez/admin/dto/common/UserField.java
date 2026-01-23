package com.ez.admin.dto.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.modules.system.entity.SysUser;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * 用户查询字段枚举
 * <p>
 * 定义支持动态查询的字段，防止 SQL 注入
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
public enum UserField {

    /**
     * 用户名
     */
    USERNAME("用户名"),

    /**
     * 昵称
     */
    NICKNAME("昵称"),

    /**
     * 手机号
     */
    PHONE_NUMBER("手机号"),

    /**
     * 邮箱
     */
    EMAIL("邮箱"),

    /**
     * 状态
     */
    STATUS("状态"),

    /**
     * 部门ID
     */
    DEPT_ID("部门ID");

    private final String name;

    UserField(String name) {
        this.name = name;
    }

    /**
     * 根据字段名获取枚举值
     *
     * @param fieldName 字段名
     * @return UserField 枚举值
     */
    public static UserField fromFieldName(String fieldName) {
        for (UserField field : values()) {
            if (field.name().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 应用查询条件到 Wrapper
     *
     * @param wrapper  LambdaQueryWrapper
     * @param operator 操作符
     * @param value    字段值
     * @return LambdaQueryWrapper
     */
    public LambdaQueryWrapper<SysUser> apply(LambdaQueryWrapper<SysUser> wrapper, Operator operator, Object value) {
        if (!StringUtils.hasText(String.valueOf(value))) {
            return wrapper;
        }

        return switch (this) {
            case USERNAME -> applyCondition(wrapper, operator, SysUser::getUsername, value);
            case NICKNAME -> applyCondition(wrapper, operator, SysUser::getNickname, value);
            case PHONE_NUMBER -> applyCondition(wrapper, operator, SysUser::getPhoneNumber, value);
            case EMAIL -> applyCondition(wrapper, operator, SysUser::getEmail, value);
            case STATUS -> applyCondition(wrapper, operator, SysUser::getStatus, value);
            case DEPT_ID -> applyCondition(wrapper, operator, SysUser::getDeptId, value);
        };
    }

    /**
     * 应用具体条件
     */
    @SuppressWarnings("unchecked")
    private static <R> LambdaQueryWrapper<SysUser> applyCondition(
            LambdaQueryWrapper<SysUser> wrapper,
            Operator operator,
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<SysUser, R> column,
            Object value) {
        return switch (operator) {
            case EQ -> wrapper.eq(column, parseValue(value));
            case NE -> wrapper.ne(column, parseValue(value));
            case GT -> wrapper.gt(column, parseComparable(value));
            case GE -> wrapper.ge(column, parseComparable(value));
            case LT -> wrapper.lt(column, parseComparable(value));
            case LE -> wrapper.le(column, parseComparable(value));
            case LIKE -> wrapper.like(column, String.valueOf(value));
            case NOT_LIKE -> wrapper.notLike(column, String.valueOf(value));
            case IN -> {
                String[] values = String.valueOf(value).split(",");
                yield wrapper.in(column, (Object[]) values);
            }
            case NOT_IN -> {
                String[] values = String.valueOf(value).split(",");
                yield wrapper.notIn(column, (Object[]) values);
            }
        };
    }

    /**
     * 解析为可比较的值
     */
    private static Comparable<?> parseComparable(Object value) {
        if (value == null) {
            return null;
        }
        String strValue = String.valueOf(value);
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            return strValue;
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
}
