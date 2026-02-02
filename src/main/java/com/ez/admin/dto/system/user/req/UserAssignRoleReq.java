package com.ez.admin.dto.system.user.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 用户分配角色请求
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "UserAssignRoleReq", description = "用户分配角色请求")
public class UserAssignRoleReq {

    @Schema(description = "用户ID", example = "1")
    @NotEmpty(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色ID列表", example = "[1, 2, 3]")
    @NotEmpty(message = "角色ID列表不能为空")
    private List<Long> roleIds;
}
