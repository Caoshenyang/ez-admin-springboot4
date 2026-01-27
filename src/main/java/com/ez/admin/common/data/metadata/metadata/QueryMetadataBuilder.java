package com.ez.admin.common.data.metadata.metadata;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.ez.admin.common.data.metadata.condition.FieldConfig;
import com.ez.admin.common.data.metadata.condition.QueryConditionSupport;
import com.ez.admin.common.core.enums.FieldType;
import com.ez.admin.common.core.enums.Operator;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询元数据构建器
 * <p>
 * 使用流式 API 构建查询元数据，替代繁琐的枚举定义
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * @Configuration
 * public class QueryMetadataConfiguration {
 *
 *     @PostConstruct
 *     public void registerMetadata() {
 *         // 用户查询元数据
 *         QueryMetadataBuilder.create(SysUser.class)
 *             .field("username", FieldType.STRING, "用户账号")
 *                 .keywordSearch()
 *                 .operators(Operator.EQ, Operator.LIKE, Operator.IN)
 *                 .column(SysUser::getUsername)
 *                 .example("admin")
 *             .field("nickname", FieldType.STRING, "用户昵称")
 *                 .keywordSearch()
 *                 .operators(Operator.LIKE)
 *                 .column(SysUser::getNickname)
 *                 .example("张三")
 *             .field("status", FieldType.INTEGER, "用户状态")
 *                 .operators(Operator.EQ, Operator.IN)
 *                 .column(SysUser::getStatus)
 *                 .dictType("sys_user_status")
 *                 .example(1)
 *             .register();
 *     }
 * }
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
public class QueryMetadataBuilder<T> {

    private final Class<T> entityClass;
    private final List<FieldConfig<T>> fields = new ArrayList<>();
    private String description;

    private QueryMetadataBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 创建构建器
     *
     * @param entityClass 实体类
     * @param <T>         实体类型
     * @return 构建器实例
     */
    public static <T> QueryMetadataBuilder<T> create(Class<T> entityClass) {
        return new QueryMetadataBuilder<>(entityClass);
    }

    /**
     * 设置资源描述
     *
     * @param description 资源描述
     * @return 构建器实例
     */
    public QueryMetadataBuilder<T> description(String description) {
        this.description = description;
        return this;
    }

    /**
     * 添加字段配置
     *
     * @param fieldCode   字段编码
     * @param type        字段类型
     * @param description 字段描述
     * @return 字段配置构建器
     */
    public FieldConfigBuilder field(String fieldCode, FieldType type, String description) {
        return new FieldConfigBuilder(fieldCode, type, description);
    }

    /**
     * 注册到查询条件支持器
     */
    @SuppressWarnings("unchecked")
    public void register() {
        if (fields.isEmpty()) {
            log.warn("跳过空元数据注册: {}", entityClass.getSimpleName());
            return;
        }

        FieldConfig<T>[] configs = fields.toArray(FieldConfig[]::new);
        QueryConditionSupport.register(entityClass, configs);

        if (description != null && !description.isBlank()) {
            QueryConditionSupport.registerDescription(entityClass, description);
        }

        log.info("注册查询元数据: {} ({} 个字段)", entityClass.getSimpleName(), fields.size());
    }

    /**
     * 字段配置构建器（内部类）
     */
    public class FieldConfigBuilder {

        private final String fieldCode;
        private final FieldType type;
        private final String description;
        private final List<Operator> operators = new ArrayList<>();
        private SFunction<T, ?> column;
        private boolean keywordSearch;
        private String dictType;

        public FieldConfigBuilder(String fieldCode, FieldType type, String description) {
            this.fieldCode = fieldCode;
            this.type = type;
            this.description = description;
        }

        /**
         * 设置支持的操作符
         */
        @SafeVarargs
        public final FieldConfigBuilder operators(Operator... ops) {
            this.operators.addAll(List.of(ops));
            return this;
        }

        /**
         * 设置列映射
         */
        public FieldConfigBuilder column(SFunction<T, ?> col) {
            this.column = col;
            return this;
        }

        /**
         * 标记为支持快捷搜索
         */
        public FieldConfigBuilder keywordSearch() {
            this.keywordSearch = true;
            return this;
        }

        /**
         * 设置字典类型
         */
        public FieldConfigBuilder dictType(String dictType) {
            this.dictType = dictType;
            return this;
        }

        /**
         * 设置示例值（仅用于元数据导出，不影响查询）
         */
        public FieldConfigBuilder example(Object example) {
            // 示例值暂不使用，保留接口以备后续扩展
            return this;
        }

        /**
         * 添加字段并返回上级构建器
         */
        public QueryMetadataBuilder<T> add() {
            if (column == null) {
                throw new IllegalStateException("字段 [" + fieldCode + "] 必须设置 column 映射");
            }

            // 创建 FieldConfig Record
            FieldConfig<T> config = new FieldConfig<>(
                    fieldCode,
                    column,
                    type,
                    keywordSearch,
                    operators.toArray(Operator[]::new),
                    dictType,
                    description
            );

            fields.add(config);
            return QueryMetadataBuilder.this;
        }
    }
}
