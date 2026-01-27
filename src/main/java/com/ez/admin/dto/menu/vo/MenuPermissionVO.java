package com.ez.admin.dto.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜单权限响应对象
 * <p>
 * 用于权限校验场景，包含菜单权限核心字段
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MenuPermissionVO", description = "菜单权限响应对象")
public class MenuPermissionVO {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "权限标识")
    private String menuPerm;

    @Schema(description = "菜单类型【1 目录 2 菜单 3 按钮】")
    private Integer menuType;

    @Schema(description = "角色ID（用于批量查询时的分组）")
    private Long roleId;
}
