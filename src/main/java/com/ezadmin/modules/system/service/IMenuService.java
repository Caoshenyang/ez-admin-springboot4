package com.ezadmin.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ezadmin.model.vo.MenuPermissionVO;
import com.ezadmin.modules.system.entity.Menu;

import java.util.List;

/**
 * <p>
 * 菜单信息表 服务类
 * </p>
 *
 * @author shenyang
 * @since 2025-12-18
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 根据角色ID查询菜单权限列表
     * @param roleIds 角色ID列表
     * @return 菜单权限列表
     */
    List<MenuPermissionVO> loadMenuPermByRoleIds(List<Long> roleIds);

    /**
     * 查询所有菜单权限列表
     * @return 所有菜单权限列表
     */
    List<MenuPermissionVO> loadAllMenuPerm();
}
