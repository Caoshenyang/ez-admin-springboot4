package com.ez.admin.dto.system.role.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 角色分配菜单请求
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "RoleAssignMenuReq", description = "角色分配菜单请求")
public class RoleAssignMenuReq {

    @Schema(description = "角色ID", example = "1")
    @NotEmpty(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "菜单ID列表", example = "[1, 2, 3]")
    @NotEmpty(message = "菜单ID列表不能为空")
    private List<Long> menuIds;
}
