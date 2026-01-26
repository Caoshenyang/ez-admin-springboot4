package com.ez.admin.common.condition;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.enums.FieldType;
import com.ez.admin.common.enums.Operator;
import com.ez.admin.dto.common.QueryCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态查询条件支持
 * <p>
 * 各模块通过注册字段配置来启用动态查询功能
 * </p>
 * <p>
 * 使用方式（从 QueryMetadata 枚举自动注册）：
 * <pre>{@code
 * // 1. 定义 QueryMetadata 枚举
 * public enum UserQueryMetadata implements QueryMetadata<SysUser> {
 *     USERNAME("username", "String", "用户账号", ...) {
 *         @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getUsername; }
 *         @Override public boolean isKeywordSearch() { return true; }
 *     },
 *     STATUS("status", "Integer", "用户状态", ...) {
 *         @Override public SFunction<SysUser, ?> getColumn() { return SysUser::getStatus; }
 *         @Override public boolean isKeywordSearch() { return false; }
 *     };
 *
 *     @Getter @AllArgsConstructor
 *     private final String field;
 *     private final String type;
 *     private final String description;
 *     private final List<Operator> operators;
 *
 *     @Override
 *     public FieldConfig<SysUser>[] toFieldConfigs() {
 *         return QueryMetadata.super.toFieldConfigs(values());
 *     }
 *
 *     // 类加载时自动注册
 *     static {
 *         QueryConditionSupport.register(SysUser.class, USERNAME.toFieldConfigs());
 *     }
 * }
 *
 * // 2. 使用快捷搜索
 * QueryConditionSupport.applyKeywordSearch(wrapper, keyword, SysUser.class);
 *
 * // 3. 使用高级查询
 * QueryConditionSupport.applyConditions(wrapper, conditions, SysUser.class);
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-24
 */
@Slf4j
public final class QueryConditionSupport {

    private static final Map<Class<?>, Map<String, FieldConfig<?>>> REGISTRY = new ConcurrentHashMap<>();

    private QueryConditionSupport() {
    }

    /**
     * 注册字段配置（幂等操作，可多次调用）
     * <p>
     * 接受从 QueryMetadataProvider 注册的字段配置数组
     * </p>
     *
     * @param entityClass 实体类
     * @param configs     字段配置数组
     * @param <T>         实体类型
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
        log.debug("注册查询字段: {} -> {}", entityClass.getSimpleName(), configMap.keySet());
    }

    /**
     * 获取所有已注册的实体类
     *
     * @return 实体类集合
     */
    public static List<Class<?>> getRegisteredEntityClasses() {
        return new ArrayList<>(REGISTRY.keySet());
    }

    /**
     * 根据实体类获取字段配置映射
     *
     * @param entityClass 实体类
     * @return 字段配置映射，如果未注册返回 null
     */
    public static Map<String, FieldConfig<?>> getFieldConfigs(Class<?> entityClass) {
        return REGISTRY.get(entityClass);
    }

    /**
     * 应用快捷搜索（keyword）
     * <p>
     * 自动对所有标记为 keywordSearch=true 的字段进行模糊匹配
     * </p>
     *
     * @param wrapper    查询包装器
     * @param keyword    搜索关键词
     * @param entityClass 实体类
     */
    @SuppressWarnings("unchecked")
    public static <T> void applyKeywordSearch(LambdaQueryWrapper<T> wrapper, String keyword, Class<T> entityClass) {
        if (!StringUtils.hasText(keyword)) {
            return;
        }

        Map<String, FieldConfig<?>> configMap = REGISTRY.get(entityClass);
        if (configMap == null || configMap.isEmpty()) {
            log.warn("快捷搜索失败: {} 未注册字段配置，请在 QueryMetadataConfiguration 中添加配置", entityClass.getSimpleName());
            return;
        }

        // 筛选出可用于快捷搜索的字段（仅字符串类型且 keywordSearch=true）
        List<FieldConfig<T>> keywordFields = configMap.values().stream()
                .filter(config -> config.keywordSearch() && config.type() == FieldType.STRING)
                .map(config -> (FieldConfig<T>) config)
                .collect(Collectors.toList());

        if (keywordFields.isEmpty()) {
            log.warn("快捷搜索失败: {} 无可用字段（STRING 类型且 keywordSearch=true）", entityClass.getSimpleName());
            return;
        }

        // 应用 OR 条件：field1 LIKE %keyword% OR field2 LIKE %keyword%
        wrapper.and(w -> {
            var it = keywordFields.iterator();
            w.like(it.next().column(), keyword);
            it.forEachRemaining(f -> w.or().like(f.column(), keyword));
        });

        log.debug("应用快捷搜索: {} keyword=[{}] fields=[{}]",
                entityClass.getSimpleName(), keyword,
                keywordFields.stream().map(FieldConfig::fieldCode).collect(Collectors.joining(", ")));
    }

    /**
     * 应用动态查询条件
     *
     * @param wrapper     查询包装器
     * @param conditions  查询条件列表
     * @param entityClass  实体类
     */
    @SuppressWarnings("unchecked")
    public static <T> void applyConditions(LambdaQueryWrapper<T> wrapper, List<QueryCondition> conditions, Class<T> entityClass) {
        Map<String, FieldConfig<?>> configMap = REGISTRY.get(entityClass);
        if (configMap == null || configMap.isEmpty()) {
            log.warn("高级查询失败: {} 未注册字段配置，请在 QueryMetadataConfiguration 中添加配置", entityClass.getSimpleName());
            return;
        }

        if (conditions == null || conditions.isEmpty()) {
            return;
        }

        for (QueryCondition condition : conditions) {
            if (condition == null || !StringUtils.hasText(condition.getField()) ||
                    !StringUtils.hasText(condition.getOperator())) {
                continue;
            }

            FieldConfig<?> config = configMap.get(condition.getField());
            if (config == null) {
                log.warn("忽略未注册字段: {}.{}", entityClass.getSimpleName(), condition.getField());
                continue;
            }

            Operator operator = Operator.fromOperator(condition.getOperator());
            if (operator == null) {
                log.warn("忽略无效操作符: {}.{} [{}]", entityClass.getSimpleName(), condition.getField(), condition.getOperator());
                continue;
            }

            try {
                switch (config.type()) {
                    case STRING -> applyStringCondition(wrapper, operator, (FieldConfig<T>) config, condition.getValue());
                    case INTEGER -> applyNumericCondition(wrapper, operator, (FieldConfig<T>) config, condition.getValue(), Integer.class);
                    case LONG -> applyNumericCondition(wrapper, operator, (FieldConfig<T>) config, condition.getValue(), Long.class);
                }
                log.debug("应用查询条件: {}.{} {} {}", entityClass.getSimpleName(), condition.getField(), operator, condition.getValue());
            } catch (IllegalArgumentException e) {
                log.warn("查询条件无效: {}.{} {} {} - {}", entityClass.getSimpleName(),
                        condition.getField(), operator, condition.getValue(), e.getMessage());
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

    /**
     * 统一处理数值类型条件（INTEGER/LONG）
     */
    @SuppressWarnings("unchecked")
    private static <T, N extends Number> void applyNumericCondition(LambdaQueryWrapper<T> wrapper, Operator operator,
                                                                    FieldConfig<T> config, String value, Class<N> numType) {
        if (!StringUtils.hasText(value)) {
            return;
        }

        if (operator == Operator.IN || operator == Operator.NOT_IN) {
            applyNumericInCondition(wrapper, config, value, operator, numType);
            return;
        }

        N numValue = parseNumber(value, numType, config.fieldCode());

        switch (operator) {
            case EQ -> wrapper.eq(config.column(), numValue);
            case NE -> wrapper.ne(config.column(), numValue);
            case GT -> wrapper.gt(config.column(), numValue);
            case GE -> wrapper.ge(config.column(), numValue);
            case LT -> wrapper.lt(config.column(), numValue);
            case LE -> wrapper.le(config.column(), numValue);
            default -> {
            }
        }
    }

    /**
     * 统一处理数值 IN 条件（INTEGER/LONG）
     */
    private static <T, N extends Number> void applyNumericInCondition(LambdaQueryWrapper<T> wrapper, FieldConfig<T> config,
                                                                       String value, Operator operator, Class<N> numType) {
        String[] values = value.split(",");
        List<N> numValues = new ArrayList<>(values.length);
        for (String v : values) {
            numValues.add(parseNumber(v.trim(), numType, config.fieldCode()));
        }

        if (operator == Operator.IN) {
            wrapper.in(config.column(), numValues);
        } else {
            wrapper.notIn(config.column(), numValues);
        }
    }

    /**
     * 统一数值解析，提供友好的错误提示
     */
    @SuppressWarnings("unchecked")
    private static <N extends Number> N parseNumber(String value, Class<N> numType, String fieldCode) {
        try {
            if (numType == Integer.class) {
                return (N) Integer.valueOf(value);
            } else if (numType == Long.class) {
                return (N) Long.valueOf(value);
            }
            throw new IllegalArgumentException("不支持的数值类型: " + numType.getSimpleName());
        } catch (NumberFormatException e) {
            String typeName = numType == Integer.class ? "整数" : "长整数";
            throw new IllegalArgumentException(String.format("字段[%s]需要%s类型，但收到: [%s]", fieldCode, typeName, value));
        }
    }
}
