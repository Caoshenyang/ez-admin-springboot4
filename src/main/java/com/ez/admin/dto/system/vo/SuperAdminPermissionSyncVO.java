package com.ez.admin.dto.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 超级管理员权限同步响应 VO
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SuperAdminPermissionSyncVO", description = "超级管理员权限同步响应")
public class SuperAdminPermissionSyncVO {

    @Schema(description = "同步的菜单数量")
    private Integer menuCount;

    @Schema(description = "角色标识", example = "SUPER_ADMIN")
    private String roleLabel;
}
