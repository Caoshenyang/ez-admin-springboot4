package com.ez.admin.service.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.data.mapstruct.MenuConverter;
import com.ez.admin.common.data.tree.TreeBuilder;
import com.ez.admin.dto.system.menu.req.MenuCreateReq;
import com.ez.admin.dto.system.menu.req.MenuUpdateReq;
import com.ez.admin.dto.system.menu.vo.MenuDetailVO;
import com.ez.admin.dto.system.menu.vo.MenuTreeVO;
import com.ez.admin.dto.system.menu.vo.RoutePermissionVO;
import com.ez.admin.modules.system.entity.SysMenu;
import com.ez.admin.modules.system.mapper.SysMenuMapper;
import com.ez.admin.modules.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单管理服务
 * <p>
 * 业务聚合层，组合原子服务实现菜单管理的复杂业务逻辑
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuService {

    private final SysMenuMapper menuMapper;
    private final SysMenuService sysMenuService;
    private final MenuConverter menuConverter;
    private final com.ez.admin.service.permission.PermissionService permissionService;

    /**
     * 创建菜单
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createMenu(MenuCreateReq request) {
        // 1. 如果有父菜单ID，检查父菜单是否存在
        if (request.getParentId() != null && !request.getParentId().equals(0L)) {
            SysMenu parentMenu = menuMapper.selectById(request.getParentId());
            if (parentMenu == null) {
                throw new EzBusinessException(ErrorCode.MENU_PARENT_NOT_FOUND);
            }
        }

        // 2. 创建菜单
        SysMenu menu = buildMenu(request);
        menuMapper.insert(menu);

        // 3. 刷新路由权限缓存（如果该菜单配置了 API 路由）
        if (menu.getApiRoute() != null && !menu.getApiRoute().isEmpty()) {
            permissionService.refreshRoutePermissions();
            log.info("路由权限缓存已刷新（菜单创建）");
        }

        // 4. 同步超级管理员权限（自动分配给 SUPER_ADMIN）
        permissionService.syncSuperAdminPermissions();

        log.info("创建菜单成功，菜单名称：{}", request.getMenuName());
    }

    /**
     * 更新菜单
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(MenuUpdateReq request) {
        // 1. 检查菜单是否存在
        SysMenu existMenu = menuMapper.selectById(request.getMenuId());
        if (existMenu == null) {
            throw new EzBusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        // 2. 如果有父菜单ID，检查父菜单是否存在
        if (request.getParentId() != null && !request.getParentId().equals(0L)) {
            // 不能将自己设置为父菜单
            if (request.getParentId().equals(request.getMenuId())) {
                throw new EzBusinessException(ErrorCode.MENU_PARENT_NOT_FOUND);
            }

            SysMenu parentMenu = menuMapper.selectById(request.getParentId());
            if (parentMenu == null) {
                throw new EzBusinessException(ErrorCode.MENU_PARENT_NOT_FOUND);
            }
        }

        // 3. 更新菜单信息
        SysMenu menu = new SysMenu();
        menu.setMenuId(request.getMenuId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuIcon(request.getMenuIcon());
        menu.setMenuLabel(request.getMenuLabel());
        menu.setParentId(request.getParentId());
        menu.setMenuSort(request.getMenuSort());
        menu.setMenuType(request.getMenuType());
        menu.setMenuPerm(request.getMenuPerm());
        menu.setRoutePath(request.getRoutePath());
        menu.setRouteName(request.getRouteName());
        menu.setComponentPath(request.getComponentPath());
        menu.setApiRoute(request.getApiRoute());
        menu.setApiMethod(request.getApiMethod());
        menu.setStatus(request.getStatus());
        menu.setDescription(request.getDescription());
        menuMapper.updateById(menu);

        // 4. 刷新路由权限缓存（如果该菜单配置了 API 路由）
        if (menu.getApiRoute() != null && !menu.getApiRoute().isEmpty()
            || existMenu.getApiRoute() != null && !existMenu.getApiRoute().isEmpty()) {
            permissionService.refreshRoutePermissions();
            log.info("路由权限缓存已刷新（菜单更新）");
        }

        // 5. 同步超级管理员权限（自动分配给 SUPER_ADMIN）
        permissionService.syncSuperAdminPermissions();

        log.info("更新菜单成功，菜单ID：{}", request.getMenuId());
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        // 1. 检查菜单是否存在
        SysMenu menu = menuMapper.selectById(menuId);
        if (menu == null) {
            throw new EzBusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        // 2. 检查菜单是否存在子菜单
        long childCount = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId));
        if (childCount > 0) {
            throw new EzBusinessException(ErrorCode.MENU_HAS_CHILDREN);
        }

        // 3. 逻辑删除菜单
        sysMenuService.removeById(menuId);

        // 4. 刷新路由权限缓存（如果该菜单配置了 API 路由）
        if (menu.getApiRoute() != null && !menu.getApiRoute().isEmpty()) {
            permissionService.refreshRoutePermissions();
            log.info("路由权限缓存已刷新（菜单删除）");
        }

        // 5. 同步超级管理员权限（移除已删除的菜单权限）
        permissionService.syncSuperAdminPermissions();

        log.info("删除菜单成功，菜单ID：{}", menuId);
    }

    /**
     * 根据ID查询菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    public MenuDetailVO getMenuById(Long menuId) {
        SysMenu menu = menuMapper.selectById(menuId);
        if (menu == null) {
            throw new EzBusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        return menuConverter.toDetailVO(menu);
    }

    /**
     * 查询菜单树
     *
     * @return 菜单树（完整的树形结构）
     */
    public List<MenuTreeVO> getMenuTree() {
        // 1. 查询所有菜单
        List<SysMenu> allMenus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getMenuSort));

        // 2. 转换为 TreeVO
        List<MenuTreeVO> menuTreeVOs = menuConverter.toTreeVOList(allMenus);

        // 3. 构建树形结构
        return TreeBuilder.build(menuTreeVOs);
    }

    /**
     * 根据API路由和HTTP方法查询权限码
     * <p>
     * 用于路由拦截式鉴权，根据请求的路由和方法查找对应的权限码
     * </p>
     *
     * @param apiRoute  API路由地址（如 /api/user）
     * @param apiMethod HTTP方法（如 GET, POST, PUT, DELETE）
     * @return 权限码字符串（如 system:user:list），如果未找到则返回 null
     */
    public String getPermByRoute(String apiRoute, String apiMethod) {
        return menuMapper.selectPermByRoute(apiRoute, apiMethod);
    }

    /**
     * 查询所有路由权限配置
     * <p>
     * 返回所有配置了 API 路由的菜单，用于权限管理和展示
     * </p>
     *
     * @return 路由权限配置列表
     */
    public List<RoutePermissionVO> getRoutePermissions() {
        // 查询所有配置了 API 路由的菜单
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .isNotNull(SysMenu::getApiRoute)
                .ne(SysMenu::getApiRoute, "")
                .orderByAsc(SysMenu::getMenuSort));

        // 转换为 VO
        return menus.stream()
                .map(menu -> RoutePermissionVO.builder()
                        .menuId(menu.getMenuId())
                        .menuName(menu.getMenuName())
                        .menuPerm(menu.getMenuPerm())
                        .apiRoute(menu.getApiRoute())
                        .apiMethod(menu.getApiMethod())
                        .menuType(menu.getMenuType())
                        .routePath(menu.getRoutePath())
                        .routeName(menu.getRouteName())
                        .componentPath(menu.getComponentPath())
                        .build())
                .collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 构建菜单实体
     */
    private SysMenu buildMenu(MenuCreateReq request) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(request.getMenuName());
        menu.setMenuIcon(request.getMenuIcon());
        menu.setMenuLabel(request.getMenuLabel());
        menu.setParentId(request.getParentId());
        menu.setMenuSort(request.getMenuSort());
        menu.setMenuType(request.getMenuType());
        menu.setMenuPerm(request.getMenuPerm());
        menu.setRoutePath(request.getRoutePath());
        menu.setRouteName(request.getRouteName());
        menu.setComponentPath(request.getComponentPath());
        menu.setApiRoute(request.getApiRoute());
        menu.setApiMethod(request.getApiMethod());
        menu.setStatus(request.getStatus());
        menu.setDescription(request.getDescription());
        return menu;
    }
}
