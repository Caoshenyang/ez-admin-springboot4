package com.ez.admin.dto.system.dict.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建字典类型请求对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "DictTypeCreateReq", description = "创建字典类型请求")
public class DictTypeCreateReq {

    @Schema(description = "字典名称", example = "用户状态")
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过 100 个字符")
    private String dictName;

    @Schema(description = "字典类型", example = "sys_user_status")
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过 100 个字符")
    private String dictType;

    @Schema(description = "状态【0 停用 1 正常】", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "描述信息", example = "用户状态字典")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}
