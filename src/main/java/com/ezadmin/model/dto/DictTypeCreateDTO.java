package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典类型创建
 */
@Data
public class DictTypeCreateDTO implements Serializable {

    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    @Schema(description = "状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述")
    private String description;
}
