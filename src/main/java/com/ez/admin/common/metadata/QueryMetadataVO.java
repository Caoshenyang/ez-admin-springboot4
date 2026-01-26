package com.ez.admin.common.metadata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 查询元数据响应 VO
 * <p>
 * 用于向前端返回资源的查询元数据，包括所有可查询的字段信息
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "QueryMetadataVO", description = "查询元数据响应")
public class QueryMetadataVO {

    /**
     * 资源名称（如 user, role, menu）
     */
    @Schema(description = "资源名称", example = "user")
    private String resource;

    /**
     * 资源描述
     */
    @Schema(description = "资源描述", example = "用户")
    private String description;

    /**
     * 查询字段列表
     */
    @Schema(description = "查询字段列表")
    private List<FieldMetadata> fields;
}
