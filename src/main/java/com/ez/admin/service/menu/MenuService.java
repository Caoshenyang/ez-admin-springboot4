package com.ez.admin.service.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.mapstruct.MenuConverter;
import com.ez.admin.dto.menu.req.MenuCreateReq;
import com.ez.admin.dto.menu.req.MenuUpdateReq;
import com.ez.admin.dto.menu.vo.MenuVO;
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
        menu.setStatus(request.getStatus());
        menu.setDescription(request.getDescription());
        menuMapper.updateById(menu);

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

        log.info("删除菜单成功，菜单ID：{}", menuId);
    }

    /**
     * 根据ID查询菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    public MenuVO getMenuById(Long menuId) {
        SysMenu menu = menuMapper.selectById(menuId);
        if (menu == null) {
            throw new EzBusinessException(ErrorCode.MENU_NOT_FOUND);
        }

        return menuConverter.toVO(menu);
    }

    /**
     * 查询菜单树
     *
     * @return 菜单树（完整的树形结构）
     */
    public List<MenuVO> getMenuTree() {
        // 1. 查询所有菜单
        List<SysMenu> allMenus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getMenuSort));

        // 2. 转换为 VO
        List<MenuVO> menuVOs = menuConverter.toVOList(allMenus);

        // 3. 构建树形结构（暂时预留，返回平铺列表）
        // TODO: 后续实现树形结构构建逻辑
        return menuVOs;
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
        menu.setStatus(request.getStatus());
        menu.setDescription(request.getDescription());
        return menu;
    }
}
