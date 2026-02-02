package com.ez.admin.dto.system.role.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量分配菜单请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "RoleBatchAssignMenuReq", description = "批量分配菜单请求")
public class RoleBatchAssignMenuReq {

    @Schema(description = "角色ID列表", example = "[1, 2, 3]")
    @NotEmpty(message = "角色ID列表不能为空")
    private List<Long> roleIds;

    @Schema(description = "菜单ID列表", example = "[1, 2, 3]")
    @NotEmpty(message = "菜单ID列表不能为空")
    private List<Long> menuIds;
}
