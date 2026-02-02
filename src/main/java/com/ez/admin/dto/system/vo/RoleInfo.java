package com.ez.admin.dto.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 角色信息（视图对象）
 * <p>
 * 用于缓存用户的角色信息，包含角色ID、标识和名称
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "RoleInfo", description = "角色信息")
public class RoleInfo {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色标识（如：SUPER_ADMIN）")
    private String roleLabel;

    @Schema(description = "角色名称（如：超级管理员）")
    private String roleName;
}
