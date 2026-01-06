package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据更新
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataUpdateDTO extends DictDataCreateDTO {

    @Schema(description = "字典数据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dictDataId;
}
