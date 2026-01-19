package com.ez.admin.system.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2026-01-19
 */
@Getter
@Setter
@ToString
@TableName("ez_admin_sys_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    @TableId(value = "menu_id", type = IdType.ASSIGN_ID)
    private Long menuId;

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单图标
     */
    @TableField("menu_icon")
    private String menuIcon;

    /**
     * 菜单标识
     */
    @TableField("menu_label")
    private String menuLabel;

    /**
     * 父级菜单ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 菜单排序
     */
    @TableField("menu_sort")
    private Integer menuSort;

    /**
     * 菜单类型【1 目录 2 菜单 3 按钮】
     */
    @TableField("menu_type")
    private Integer menuType;

    /**
     * 权限标识
     */
    @TableField("menu_perm")
    private String menuPerm;

    /**
     * 路由地址
     */
    @TableField("route_path")
    private String routePath;

    /**
     * 路由名称
     */
    @TableField("route_name")
    private String routeName;

    /**
     * 组件路径
     */
    @TableField("component_path")
    private String componentPath;

    /**
     * 菜单状态【0 停用 1 正常】
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建者
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 描述信息
     */
    @TableField("description")
    private String description;

    /**
     * 是否删除【0 正常 1 已删除】
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
