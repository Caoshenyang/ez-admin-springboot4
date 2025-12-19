package com.ezadmin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单创建
 */
@Data
public class MenuCreateDTO implements Serializable {

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuName;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "菜单标识")
    private String menuLabel;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "菜单排序")
    private Integer menuSort;

    @Schema(description = "菜单类型【1 目录 2 菜单 3 按钮】")
    private Integer menuType;

    @Schema(description = "权限标识")
    private String menuPerm;

    @Schema(description = "路由地址")
    private String routePath;

    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "组件路径")
    private String componentPath;

    @Schema(description = "菜单状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "描述信息")
    private String description;
}
