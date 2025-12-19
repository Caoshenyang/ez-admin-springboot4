package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型更新
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeUpdateDTO extends DictTypeCreateDTO {

    @Schema(description = "字典ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dictId;
}
