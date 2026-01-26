package com.ez.admin.dto.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单响应对象（树形结构）
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Getter
@Builder
@Schema(name = "MenuVO", description = "菜单响应（树形结构）")
public class MenuVO {

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "菜单名称")
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

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "子菜单列表（树形结构）")
    private List<MenuVO> children;
}
