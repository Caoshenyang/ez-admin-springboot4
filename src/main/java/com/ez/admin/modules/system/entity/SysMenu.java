package com.ez.admin.modules.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜单信息表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_menu")
@Schema(name = "SysMenu", description = "菜单信息表")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID")
    @TableId(value = "menu_id", type = IdType.ASSIGN_ID)
    private Long menuId;

    @TableField("menu_name")
    @Schema(description = "菜单名称")
    private String menuName;

    @TableField("menu_icon")
    @Schema(description = "菜单图标")
    private String menuIcon;

    @TableField("menu_label")
    @Schema(description = "菜单标识")
    private String menuLabel;

    @TableField("parent_id")
    @Schema(description = "父级菜单ID")
    private Long parentId;

    @TableField("menu_sort")
    @Schema(description = "菜单排序")
    private Integer menuSort;

    @TableField("menu_type")
    @Schema(description = "菜单类型【1 目录 2 菜单 3 按钮】")
    private Integer menuType;

    @TableField("menu_perm")
    @Schema(description = "权限标识")
    private String menuPerm;

    @TableField("route_path")
    @Schema(description = "路由地址")
    private String routePath;

    @TableField("route_name")
    @Schema(description = "路由名称")
    private String routeName;

    @Schema(description = "组件路径")
    @TableField("component_path")
    private String componentPath;

    @TableField("api_route")
    @Schema(description = "后端API路由地址")
    private String apiRoute;

    @TableField("api_method")
    @Schema(description = "HTTP方法【GET POST PUT DELETE PATCH】")
    private String apiMethod;

    @TableField("status")
    @Schema(description = "菜单状态【0 停用 1 正常】")
    private Integer status;

    @Schema(description = "创建者")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("description")
    @Schema(description = "描述信息")
    private String description;

    @TableLogic
    @TableField("is_deleted")
    @Schema(description = "是否删除【0 正常 1 已删除】")
    private Integer isDeleted;
}
