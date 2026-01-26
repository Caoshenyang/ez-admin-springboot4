package com.ez.admin.dto.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 字典数据列表响应对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "DictDataListVO", description = "字典数据列表响应")
public class DictDataListVO {

    @Schema(description = "字典数据ID")
    private Long dictDataId;

    @Schema(description = "字典类型ID")
    private Long dictId;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "字典排序")
    private Integer dictSort;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认【0 否 1 是】")
    private Integer isDefault;

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
