package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据创建
 */
@Data
public class DictDataCreateDTO implements Serializable {

    @Schema(description = "字典类型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dictId;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictLabel;

    @Schema(description = "字典键值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictValue;

    @Schema(description = "字典排序")
    private Integer dictSort;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认【0 否 1 是】")
    private Integer isDefault;

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述信息")
    private String description;
}
