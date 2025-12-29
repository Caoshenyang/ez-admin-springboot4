package com.ezadmin.model.vo;

import com.ezadmin.common.response.tree.TreeNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 类名: MenuTreeVO
 * 功能描述: 菜单树
 *
 * @author shenyang
 * @since 2025/3/21 9:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuTreeVO extends TreeNode {

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
    private String routePath;

    /**
     * 组件路径
     */
    private String componentPath;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 菜单类型：0目录，1菜单，2按钮
     */
    private Integer menuType;

    /**
     * 权限标识
     */
    private String menuPerm;

    /**
     * 排序
     */
    private Integer menuSort;

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

    @JsonIgnore
    @Override
    public Long getNodeId() {
        return menuId;
    }

    @Override
    public Long getParentId() {
        return parentId;
    }
}
