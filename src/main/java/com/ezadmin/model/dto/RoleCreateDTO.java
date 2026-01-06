package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色创建请求
 */
@Data
public class RoleCreateDTO implements Serializable {

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @Schema(description = "角色权限字符标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleLabel;

    @Schema(description = "排序")
    private Integer roleSort;

    @Schema(description = "数据范围【1 仅本人 2 本部门 3 本部门及以下 4 自定义 5 全部】")
    private Integer dataScope;

    @Schema(description = "角色状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "绑定的菜单ID列表")
    private List<Long> menuIds;

    @Schema(description = "当 dataScope=4 时的数据权限部门ID列表")
    private List<Long> deptIds;
}
