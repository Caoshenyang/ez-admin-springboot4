package com.ez.admin.system.entity;

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
 * 菜单信息表
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Schema(description = "菜单信息表")
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单ID", example = "1")
    @TableId(value = "menu_id", type = IdType.ASSIGN_ID)
    private Long menuId;

    @Schema(description = "菜单名称", example = "系统管理")
    @TableField("menu_name")
    private String menuName;

    @Schema(description = "菜单图标", example = "setting")
    @TableField("menu_icon")
    private String menuIcon;

    @Schema(description = "菜单标识", example = "system")
    @TableField("menu_label")
    private String menuLabel;

    @Schema(description = "父级菜单ID（0表示根菜单）", example = "0")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "菜单排序", example = "1")
    @TableField("menu_sort")
    private Integer menuSort;

    @Schema(description = "菜单类型（1=目录，2=菜单，3=按钮）", example = "2")
    @TableField("menu_type")
    private Integer menuType;

    @Schema(description = "权限标识", example = "system:user:list")
    @TableField("menu_perm")
    private String menuPerm;

    @Schema(description = "路由地址", example = "/system/user")
    @TableField("route_path")
    private String routePath;

    @Schema(description = "路由名称", example = "SystemUser")
    @TableField("route_name")
    private String routeName;

    @Schema(description = "组件路径", example = "/system/user/index")
    @TableField("component_path")
    private String componentPath;

    @Schema(description = "菜单状态（0=停用，1=正常）", example = "1")
    @TableField("status")
    private Integer status;

    @Schema(description = "创建者", example = "admin")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间", example = "2026-01-19T10:00:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新者", example = "admin")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @Schema(description = "更新时间", example = "2026-01-19T10:00:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "描述信息", example = "系统管理菜单")
    @TableField("description")
    private String description;

    @Schema(description = "是否删除（0=正常，1=已删除）", example = "0")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
