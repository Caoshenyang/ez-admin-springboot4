package com.ez.admin.dto.role.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色状态切换请求对象
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Data
@Schema(name = "RoleStatusChangeReq", description = "角色状态切换请求")
public class RoleStatusChangeReq {

    @Schema(description = "角色ID", example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
