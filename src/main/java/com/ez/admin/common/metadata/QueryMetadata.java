package com.ez.admin.common.metadata;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.condition.FieldConfig;
import com.ez.admin.common.enums.Operator;

import java.util.List;

/**
 * 查询元数据接口（泛型，类型安全）
 * <p>
 * 定义可查询字段的元数据规范，各模块枚举实现此接口
 * </p>
 * <p>
 * 设计理念：
 * <ul>
 *   <li>泛型 T：实体类型（如 SysUser），提供类型安全的 Lambda 引用</li>
 *   <li>枚举实现：利用枚举的特性，避免手动定义 values() 方法</li>
 *   <li>默认方法：提供通用的 FieldConfig 生成逻辑</li>
 * </ul>
 * </p>
 *
 * @param <T> 实体类型，如 SysUser、SysRole 等
 * @author ez-admin
 * @since 2026-01-26
 */
public interface QueryMetadata<T> {

    // ========== 必须实现的方法 ==========

    /**
     * 获取数据库字段名（驼峰命名）
     *
     * @return 字段名，如 "username"
     */
    String getField();

    /**
     * 获取 Java 类型
     *
     * @return 类型名称，如 "String", "Integer", "Long"
     */
    String getType();

    /**
     * 获取中文描述
     *
     * @return 描述，如 "用户账号"
     */
    String getDescription();

    /**
     * 获取支持的操作符列表
     *
     * @return 操作符列表，如 [EQ, LIKE, IN]
     */
    List<Operator> getOperators();

    /**
     * 获取实体的 Lambda 字段引用（类型安全）
     * <p>
     * 示例：SysUser::getUsername
     * </p>
     *
     * @return Lambda 字段引用
     */
    SFunction<T, ?> getColumn();

    /**
     * 是否用于快捷搜索
     * <p>
     * true: 在 keyword 搜索中被使用（OR 连接的 LIKE 查询）
     * false: 仅在高级查询中使用
     * </p>
     *
     * @return 是否用于快捷搜索
     */
    boolean isKeywordSearch();

    // ========== 可选重写的方法（有默认实现） ==========

    /**
     * 获取所有字段的元数据列表
     * <p>
     * 子类需要重写此方法，返回该资源的所有可查询字段
     * </p>
     *
     * @return 字段元数据列表
     */
    default List<FieldMetadata> getFields() {
        throw new UnsupportedOperationException(
                "请在枚举中实现此方法，例如：\n" +
                "@Override\n" +
                "public List<FieldMetadata> getFields() {\n" +
                "    return Arrays.stream(values())\n" +
                "            .map(this::toFieldMetadata)\n" +
                "            .collect(Collectors.toList());\n" +
                "}"
        );
    }

    /**
     * 将当前枚举项转换为 FieldMetadata
     *
     * @return FieldMetadata
     */
    default FieldMetadata toFieldMetadata() {
        return FieldMetadata.builder()
                .field(getField())
                .type(getType())
                .description(getDescription())
                .operators(getOperators().stream()
                        .map(Operator::getCode)
                        .collect(java.util.stream.Collectors.toList()))
                .required(isRequired())
                .example(getExample())
                .dictType(getDictType())
                .build();
    }

    /**
     * 获取字典类型（用于下拉选择）
     *
     * @return 字典类型代码，如 "sys_user_status"，无字典返回 null
     */
    default String getDictType() {
        return null;
    }

    /**
     * 获取示例值（前端展示用）
     *
     * @return 示例值，如 "admin"，无示例返回 null
     */
    default Object getExample() {
        return null;
    }

    /**
     * 是否必填
     *
     * @return 是否必填，默认 false
     */
    default Boolean isRequired() {
        return false;
    }

    // ========== 默认方法（通用逻辑，子类无需实现） ==========

    /**
     * 获取所有字段的 FieldConfig 数组（用于动态查询注册）
     * <p>
     * 自动根据枚举定义生成 FieldConfig 数组
     * </p>
     *
     * @return 字段配置数组
     */
    default FieldConfig<T>[] toFieldConfigs() {
        // 子类枚举需要重写此方法，调用自身的 values() 方法
        throw new UnsupportedOperationException(
                "请在枚举中实现此方法，例如：\n" +
                "@Override\n" +
                "public FieldConfig<SysUser>[] toFieldConfigs() {\n" +
                "    return QueryMetadata.super.toFieldConfigs(values());\n" +
                "}"
        );
    }

    /**
     * 根据枚举值生成 FieldConfig 数组（工具方法）
     *
     * @param enums 枚举值数组
     * @return FieldConfig 数组
     */
    @SuppressWarnings("unchecked")
    default FieldConfig<T>[] toFieldConfigs(QueryMetadata<T>[] enums) {
        FieldConfig<T>[] configs = new FieldConfig[enums.length];

        for (int i = 0; i < enums.length; i++) {
            QueryMetadata<T> meta = enums[i];
            String type = meta.getType();

            configs[i] = switch (type) {
                case "String" -> FieldConfig.string(
                        meta.getField(),
                        (SFunction<T, String>) meta.getColumn(),
                        meta.isKeywordSearch()
                );
                case "Integer" -> FieldConfig.integer(
                        meta.getField(),
                        (SFunction<T, Integer>) meta.getColumn(),
                        meta.isKeywordSearch()
                );
                case "Long" -> FieldConfig.longNum(
                        meta.getField(),
                        (SFunction<T, Long>) meta.getColumn(),
                        meta.isKeywordSearch()
                );
                default -> throw new IllegalArgumentException(
                        "不支持的字段类型: " + type + "，字段: " + meta.getField()
                );
            };
        }

        return configs;
    }
}
