package com.ez.admin.dto.system.role.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新角色请求对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Data
@Schema(name = "RoleUpdateReq", description = "更新角色请求")
public class RoleUpdateReq {

    @Schema(description = "角色ID", example = "1")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @Schema(description = "角色名称", example = "管理员")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过 50 个字符")
    private String roleName;

    @Schema(description = "角色权限字符标识", example = "admin")
    @NotBlank(message = "角色权限字符标识不能为空")
    @Size(max = 100, message = "角色权限字符标识长度不能超过 100 个字符")
    private String roleLabel;

    @Schema(description = "排序", example = "1")
    @NotNull(message = "排序不能为空")
    private Integer roleSort;

    @Schema(description = "数据范围【1 仅本人 2 本部门 3 本部门及以下 4 自定义 5 全部数据】", example = "5")
    @NotNull(message = "数据范围不能为空")
    private Integer dataScope;

    @Schema(description = "角色状态【0 停用 1 正常】", example = "1")
    @NotNull(message = "角色状态不能为空")
    private Integer status;

    @Schema(description = "菜单ID列表", example = "[1, 2, 3]")
    private List<Long> menuIds;

    @Schema(description = "部门ID列表（当数据范围为自定义时生效）", example = "[1, 2]")
    private List<Long> deptIds;

    @Schema(description = "描述信息", example = "系统管理员角色")
    @Size(max = 200, message = "描述长度不能超过 200 个字符")
    private String description;
}
