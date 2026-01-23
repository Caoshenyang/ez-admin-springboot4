package com.ez.admin.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询过滤器
 * <p>
 * 用于前端动态组合查询条件
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
@Schema(name = "Filter", description = "查询过滤器")
public class Filter {

    @Schema(description = "字段名（枚举值，防 SQL 注入）", example = "USERNAME")
    private String field;

    @Schema(description = "操作符（EQ/NE/GT/GE/LT/LE/LIKE/NOT_LIKE/IN/NOT_IN）", example = "LIKE")
    private String operator;

    @Schema(description = "字段值", example = "admin")
    private String value;
}
