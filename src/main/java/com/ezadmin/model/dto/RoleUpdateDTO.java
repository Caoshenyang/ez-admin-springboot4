package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色更新请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleUpdateDTO extends RoleCreateDTO {

    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;
}
