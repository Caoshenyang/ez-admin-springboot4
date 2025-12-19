package com.ezadmin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色详情视图
 */
@Data
public class RoleDetailVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色权限字符标识")
    private String roleLabel;

    @Schema(description = "排序")
    private Integer roleSort;

    @Schema(description = "数据范围")
    private Integer dataScope;

    @Schema(description = "角色状态")
    private Integer status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "绑定的菜单ID列表")
    private List<Long> menuIds;

    @Schema(description = "数据权限部门ID列表（dataScope=4时）")
    private List<Long> deptIds;
}
