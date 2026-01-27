package com.ez.admin.dto.dict.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 字典数据详情响应对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Builder
@Schema(name = "DictDataDetailVO", description = "字典数据详情响应对象")
public class DictDataDetailVO {

    @Schema(description = "字典数据ID")
    private Long dictDataId;

    @Schema(description = "字典ID")
    private Long dictId;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "排序")
    private Integer dictSort;

    @Schema(description = "CSS类名（如 tag-success）")
    private String listClass;

    @Schema(description = "是否默认")
    private Integer isDefault;

    @Schema(description = "状态（0=停用，1=正常）")
    private Integer status;

    @Schema(description = "备注")
    private String description;
}
