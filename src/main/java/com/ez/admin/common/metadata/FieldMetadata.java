package com.ez.admin.common.metadata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 字段元数据
 * <p>
 * 描述单个可查询字段的完整信息，包括字段名、类型、支持的操作符等
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "FieldMetadata", description = "字段元数据")
public class FieldMetadata {

    /**
     * 数据库字段名（驼峰命名，如 username）
     */
    @Schema(description = "数据库字段名", example = "username")
    private String field;

    /**
     * Java 类型（String, Integer, LocalDateTime 等）
     */
    @Schema(description = "Java 类型", example = "String")
    private String type;

    /**
     * 中文描述
     */
    @Schema(description = "中文描述", example = "用户名")
    private String description;

    /**
     * 支持的操作符列表（EQ, LIKE, IN 等）
     */
    @Schema(description = "支持的操作符列表", example = "[\"EQ\", \"LIKE\", \"IN\"]")
    private List<String> operators;

    /**
     * 是否必填
     */
    @Schema(description = "是否必填", example = "false")
    private Boolean required;

    /**
     * 示例值
     */
    @Schema(description = "示例值", example = "admin")
    private Object example;

    /**
     * 字典类型（用于下拉选项）
     * <p>
     * 如果该字段有对应的字典类型（如状态字段），前端可以根据此值获取字典选项
     * </p>
     */
    @Schema(description = "字典类型（用于下拉选择）", example = "sys_user_status")
    private String dictType;
}
