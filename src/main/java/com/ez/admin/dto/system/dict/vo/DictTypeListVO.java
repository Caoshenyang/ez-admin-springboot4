package com.ez.admin.dto.system.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 字典类型列表响应对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "DictTypeListVO", description = "字典类型列表响应")
public class DictTypeListVO {

    @Schema(description = "字典ID")
    private Long dictId;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
