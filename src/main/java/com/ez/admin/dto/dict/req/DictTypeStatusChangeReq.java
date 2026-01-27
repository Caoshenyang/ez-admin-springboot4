package com.ez.admin.dto.dict.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 字典类型状态切换请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "DictTypeStatusChangeReq", description = "字典类型状态切换请求")
public class DictTypeStatusChangeReq {

    @Schema(description = "字典ID", example = "1")
    @NotNull(message = "字典ID不能为空")
    private Long dictId;

    @Schema(description = "状态（0=停用，1=正常）", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
