package com.ezadmin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 菜单权限VO
 * </p>
 *
 * @author 曹申阳
 * @since 2025-12-17 17:09:58
 */
@Data
public class MenuPermissionVO {
    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 父菜单ID，0表示根菜单
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单类型：0目录，1菜单，2按钮
     */
    private Integer type;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否隐藏：0显示，1隐藏
     */
    private Integer hidden;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 状态：0禁用，1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
