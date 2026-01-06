package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单更新
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuUpdateDTO extends MenuCreateDTO {

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long menuId;
}
