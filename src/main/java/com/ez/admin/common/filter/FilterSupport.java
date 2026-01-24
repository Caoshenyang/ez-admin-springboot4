package com.ez.admin.common.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.dto.common.QueryCondition;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用查询过滤器支持
 * <p>
 * 各模块通过注册字段配置来启用动态过滤功能
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
 * QueryConditionSupport.applyQueryConditions(wrapper, conditions, SysUser.class);
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-24
 */
public final class QueryConditionSupport {

    /**
     * 字段配置注册表
     * Key: 实体类
     * Value: fieldCode → FieldConfig
     */
    private static final Map<Class<?>, Map<String, FieldConfig<?>>> REGISTRY = new ConcurrentHashMap<>();

    private QueryConditionSupport() {
    }

    /**
     * 注册字段配置（幂等操作，可多次调用）
     *
     * @param entityClass 实体类
     * @param configs     字段配置列表
     * @param <T>         实体类型
     */
    @SafeVarargs
    public static <T> void register(Class<T> entityClass, FieldConfig<T>... configs) {
        // 幂等操作：如果已注册则不重复注册
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
     * 应用动态过滤条件
     *
     * @param wrapper     查询包装器
     * @param conditions     过滤条件列表
     * @param entityClass 实体类
     * @param <T>         实体类型
     */
    @SuppressWarnings("unchecked")
    public static <T> void applyQueryConditions(LambdaQueryWrapper<T> wrapper, List<QueryCondition> conditions, Class<T> entityClass) {
        Map<String, FieldConfig<?>> configMap = REGISTRY.get(entityClass);
        if (configMap == null || configMap.isEmpty()) {
            return;
        }

        for (QueryCondition filter : conditions) {
            if (filter == null || !StringUtils.hasText(filter.getField()) ||
                    !StringUtils.hasText(filter.getOperator())) {
                continue;
            }

            FieldConfig<?> config = configMap.get(filter.getField());
            if (config == null) {
                continue; // 忽略未注册字段
            }

            Operator operator = Operator.fromOperator(filter.getOperator());
            if (operator == null) {
                continue; // 忽略无效操作符
            }

            // 根据字段类型应用条件
            switch (config.type()) {
                case STRING -> applyStringCondition(wrapper, operator, (FieldConfig<T>) config, filter.getValue());
                case INTEGER -> applyIntCondition(wrapper, operator, (FieldConfig<T>) config, filter.getValue());
                case LONG -> applyLongCondition(wrapper, operator, (FieldConfig<T>) config, filter.getValue());
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
                // GT, GE, LT, LE 对字符串无意义，忽略
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
                    // LIKE, NOT_LIKE 对数值字段无意义
                }
            }
        } catch (NumberFormatException e) {
            // 忽略无效数值
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
                    // LIKE, NOT_LIKE 对数值字段无意义
                }
            }
        } catch (NumberFormatException e) {
            // 忽略无效数值
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
            // 忽略无效数值
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
            // 忽略无效数值
        }
    }
}
