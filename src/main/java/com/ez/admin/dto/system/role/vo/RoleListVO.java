package com.ez.admin.dto.system.role.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 角色列表响应对象
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "RoleListVO", description = "角色列表响应")
public class RoleListVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符标识")
    private String roleLabel;

    @Schema(description = "排序")
    private Integer roleSort;

    @Schema(description = "数据范围【1 仅本人 2 本部门 3 本部门及以下 4 自定义 5 全部数据】")
    private Integer dataScope;

    @Schema(description = "角色状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
