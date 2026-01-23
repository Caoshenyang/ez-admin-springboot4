package com.ez.admin.dto.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 角色信息响应对象
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@Schema(name = "RoleVO", description = "角色信息响应")
public class RoleVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限标识")
    private String roleLabel;
}
