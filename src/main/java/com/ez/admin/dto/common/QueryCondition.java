package com.ez.admin.dto.common;

import com.ez.admin.common.enums.Operator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询条件
 * <p>
 * 用于前端动态组合查询条件
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Data
@Schema(name = "QueryCondition", description = "查询条件")
public class QueryCondition {

    @Schema(description = "字段名（驼峰命名，如 username）", example = "username")
    private String field;

    @Schema(description = "操作符（EQ/NE/GT/GE/LT/LE/LIKE/NOT_LIKE/IN/NOT_IN）", example = "LIKE")
    private String operator;

    @Schema(description = "字段值", example = "admin")
    private String value;
}
