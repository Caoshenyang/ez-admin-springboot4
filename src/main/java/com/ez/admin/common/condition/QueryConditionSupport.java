package com.ez.admin.common.condition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.dto.common.QueryCondition;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态查询条件支持
 * <p>
 * 各模块通过注册字段配置来启用动态查询功能
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 1. 定义字段配置（驼峰命名）
 * QueryConditionSupport.register(SysUser.class,
 *     FieldConfig.string("username", SysUser::getUsername),
 *     FieldConfig.integer("status", SysUser::getStatus)
 * );
 *
 * // 2. 使用
 * QueryConditionSupport.applyConditions(wrapper, conditions, SysUser.class);
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-24
 */
public final class QueryConditionSupport {

    private static final Map<Class<?>, Map<String, FieldConfig<?>>> REGISTRY = new ConcurrentHashMap<>();

    private QueryConditionSupport() {
    }

    /**
     * 注册字段配置（幂等操作，可多次调用）
     */
    @SafeVarargs
    public static <T> void register(Class<T> entityClass, FieldConfig<T>... configs) {
        if (REGISTRY.containsKey(entityClass)) {
            return;
        }
        Map<String, FieldConfig<?>> configMap = new ConcurrentHashMap<>();
        for (FieldConfig<T> config : configs) {
            configMap.put(config.fieldCode(), config);
        }
        REGISTRY.put(entityClass, configMap);
    }

    /**
     * 应用动态查询条件
     */
    @SuppressWarnings("unchecked")
    public static <T> void applyConditions(LambdaQueryWrapper<T> wrapper, List<QueryCondition> conditions, Class<T> entityClass) {
        Map<String, FieldConfig<?>> configMap = REGISTRY.get(entityClass);
        if (configMap == null || configMap.isEmpty()) {
            return;
        }

        for (QueryCondition condition : conditions) {
            if (condition == null || !StringUtils.hasText(condition.getField()) ||
                    !StringUtils.hasText(condition.getOperator())) {
                continue;
            }

            FieldConfig<?> config = configMap.get(condition.getField());
            if (config == null) {
                continue;
            }

            Operator operator = Operator.fromOperator(condition.getOperator());
            if (operator == null) {
                continue;
            }

            switch (config.type()) {
                case STRING -> applyStringCondition(wrapper, operator, (FieldConfig<T>) config, condition.getValue());
                case INTEGER -> applyIntCondition(wrapper, operator, (FieldConfig<T>) config, condition.getValue());
                case LONG -> applyLongCondition(wrapper, operator, (FieldConfig<T>) config, condition.getValue());
            }
        }
    }

    private static <T> void applyStringCondition(LambdaQueryWrapper<T> wrapper, Operator operator,
                                                  FieldConfig<T> config, String value) {
        switch (operator) {
            case EQ -> wrapper.eq(StringUtils.hasText(value), config.column(), value);
            case NE -> wrapper.ne(StringUtils.hasText(value), config.column(), value);
            case LIKE -> wrapper.like(StringUtils.hasText(value), config.column(), value);
            case NOT_LIKE -> wrapper.notLike(StringUtils.hasText(value), config.column(), value);
            case IN, NOT_IN -> {
                if (StringUtils.hasText(value)) {
                    String[] values = value.split(",");
                    if (operator == Operator.IN) {
                        wrapper.in(config.column(), (Object[]) values);
                    } else {
                        wrapper.notIn(config.column(), (Object[]) values);
                    }
                }
            }
            default -> {
            }
        }
    }

    private static <T> void applyIntCondition(LambdaQueryWrapper<T> wrapper, Operator operator,
                                               FieldConfig<T> config, String value) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        try {
            int numValue = Integer.parseInt(value);
            switch (operator) {
                case EQ -> wrapper.eq(config.column(), numValue);
                case NE -> wrapper.ne(config.column(), numValue);
                case GT -> wrapper.gt(config.column(), numValue);
                case GE -> wrapper.ge(config.column(), numValue);
                case LT -> wrapper.lt(config.column(), numValue);
                case LE -> wrapper.le(config.column(), numValue);
                case IN -> applyIntInCondition(wrapper, config, value, true);
                case NOT_IN -> applyIntInCondition(wrapper, config, value, false);
                default -> {
                }
            }
        } catch (NumberFormatException e) {
        }
    }

    private static <T> void applyLongCondition(LambdaQueryWrapper<T> wrapper, Operator operator,
                                                FieldConfig<T> config, String value) {
        if (!StringUtils.hasText(value)) {
            return;
        }
        try {
            long numValue = Long.parseLong(value);
            switch (operator) {
                case EQ -> wrapper.eq(config.column(), numValue);
                case NE -> wrapper.ne(config.column(), numValue);
                case GT -> wrapper.gt(config.column(), numValue);
                case GE -> wrapper.ge(config.column(), numValue);
                case LT -> wrapper.lt(config.column(), numValue);
                case LE -> wrapper.le(config.column(), numValue);
                case IN -> applyLongInCondition(wrapper, config, value, true);
                case NOT_IN -> applyLongInCondition(wrapper, config, value, false);
                default -> {
                }
            }
        } catch (NumberFormatException e) {
        }
    }

    private static <T> void applyIntInCondition(LambdaQueryWrapper<T> wrapper, FieldConfig<T> config,
                                                 String value, boolean isIn) {
        String[] values = value.split(",");
        List<Integer> intValues = new ArrayList<>(values.length);
        try {
            for (String v : values) {
                intValues.add(Integer.parseInt(v.trim()));
            }
            if (isIn) {
                wrapper.in(config.column(), intValues);
            } else {
                wrapper.notIn(config.column(), intValues);
            }
        } catch (NumberFormatException e) {
        }
    }

    private static <T> void applyLongInCondition(LambdaQueryWrapper<T> wrapper, FieldConfig<T> config,
                                                  String value, boolean isIn) {
        String[] values = value.split(",");
        List<Long> longValues = new ArrayList<>(values.length);
        try {
            for (String v : values) {
                longValues.add(Long.parseLong(v.trim()));
            }
            if (isIn) {
                wrapper.in(config.column(), longValues);
            } else {
                wrapper.notIn(config.column(), longValues);
            }
        } catch (NumberFormatException e) {
        }
    }
}
