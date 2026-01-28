package com.ez.admin.dto.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 字典类型详情响应对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "DictTypeDetailVO", description = "字典类型详情响应")
public class DictTypeDetailVO {

    @Schema(description = "字典ID")
    private Long dictId;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述信息")
    private String description;

    @Schema(description = "创建者ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
