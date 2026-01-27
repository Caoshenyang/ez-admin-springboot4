package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.modules.system.entity.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单信息表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据API路由和HTTP方法查询权限码
     * <p>
     * 用于路由拦截式鉴权，根据请求的路由和方法查找对应的权限码
     * </p>
     *
     * @param apiRoute API路由地址（如 /api/user）
     * @param apiMethod HTTP方法（如 GET, POST, PUT, DELETE）
     * @return 权限码字符串（如 system:user:list），如果未找到则返回 null
     */
    String selectPermByRoute(@Param("apiRoute") String apiRoute, @Param("apiMethod") String apiMethod);

    /**
     * 查询所有启用状态的菜单
     *
     * @return 启用状态的菜单列表
     */
    default List<SysMenu> selectAllActiveMenus() {
        return this.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getStatus, SystemConstants.STATUS_NORMAL)
                .eq(SysMenu::getIsDeleted, SystemConstants.NOT_DELETED)
                .orderByAsc(SysMenu::getMenuSort));
    }

    /**
     * 根据菜单ID列表查询启用状态的菜单
     *
     * @param menuIds 菜单ID列表
     * @return 菜单列表
     */
    default List<SysMenu> selectActiveMenusByIds(List<Long> menuIds) {
        return this.selectList(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getMenuId, menuIds)
                .eq(SysMenu::getStatus, SystemConstants.STATUS_NORMAL)
                .eq(SysMenu::getIsDeleted, SystemConstants.NOT_DELETED));
    }

}
