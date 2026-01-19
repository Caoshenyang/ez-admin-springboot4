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

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", example = "1")
    @TableId(value = "menu_id", type = IdType.ASSIGN_ID)
    private Long menuId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称", example = "系统管理")
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单图标
     */
    @Schema(description = "菜单图标", example = "setting")
    @TableField("menu_icon")
    private String menuIcon;

    /**
     * 菜单标识
     */
    @Schema(description = "菜单标识", example = "system")
    @TableField("menu_label")
    private String menuLabel;

    /**
     * 父级菜单ID
     */
    @Schema(description = "父级菜单ID（0表示根菜单）", example = "0")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 菜单排序
     */
    @Schema(description = "菜单排序", example = "1")
    @TableField("menu_sort")
    private Integer menuSort;

    /**
     * 菜单类型【1 目录 2 菜单 3 按钮】
     */
    @Schema(description = "菜单类型（1=目录，2=菜单，3=按钮）", example = "2")
    @TableField("menu_type")
    private Integer menuType;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识", example = "system:user:list")
    @TableField("menu_perm")
    private String menuPerm;

    /**
     * 路由地址
     */
    @Schema(description = "路由地址", example = "/system/user")
    @TableField("route_path")
    private String routePath;

    /**
     * 路由名称
     */
    @Schema(description = "路由名称", example = "SystemUser")
    @TableField("route_name")
    private String routeName;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径", example = "/system/user/index")
    @TableField("component_path")
    private String componentPath;

    /**
     * 菜单状态【0 停用 1 正常】
     */
    @Schema(description = "菜单状态（0=停用，1=正常）", example = "1")
    @TableField("status")
    private Integer status;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-19T10:00:00")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @Schema(description = "更新者", example = "admin")
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2026-01-19T10:00:00")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 描述信息
     */
    @Schema(description = "描述信息", example = "系统管理菜单")
    @TableField("description")
    private String description;

    /**
     * 是否删除【0 正常 1 已删除】
     */
    @Schema(description = "是否删除（0=正常，1=已删除）", example = "0")
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
