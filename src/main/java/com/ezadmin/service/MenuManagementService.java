package com.ezadmin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ezadmin.common.response.tree.TreeBuilder;
import com.ezadmin.model.dto.MenuCreateDTO;
import com.ezadmin.model.dto.MenuUpdateDTO;
import com.ezadmin.model.query.MenuQuery;
import com.ezadmin.model.vo.MenuTreeVO;
import com.ezadmin.modules.system.entity.Menu;
import com.ezadmin.modules.system.service.IMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜单管理业务
 */
@Service
@RequiredArgsConstructor
public class MenuManagementService {

    private final IMenuService menuService;

    /**
     * 菜单树查询
     */
    public List<MenuTreeVO> tree(MenuQuery query) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            wrapper = query.buildWrapper();
            if (query.getStatus() != null) {
                wrapper.eq(Menu::getStatus, query.getStatus());
            }
        }
        wrapper.orderByAsc(Menu::getMenuSort);
        List<Menu> menus = menuService.list(wrapper);
        List<MenuTreeVO> menuTreeVOS = menus.stream()
            .map(this::toTreeVO)
            .toList();
        return TreeBuilder.buildTree(menuTreeVOS);
    }

    public Menu detail(Long menuId) {
        return menuService.getById(menuId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(MenuCreateDTO dto) {
        Menu menu = BeanUtil.copyProperties(dto, Menu.class);
        menuService.save(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(MenuUpdateDTO dto) {
        Menu menu = BeanUtil.copyProperties(dto, Menu.class);
        menuService.updateById(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long menuId) {
        // 先检查是否有子节点
        long children = menuService.lambdaQuery().eq(Menu::getParentId, menuId).count();
        if (children > 0) {
            throw new IllegalStateException("请先删除子菜单");
        }
        menuService.removeById(menuId);
    }

    public List<Menu> listAll() {
        return menuService.lambdaQuery().orderByAsc(Menu::getMenuSort).list();
    }

    private MenuTreeVO toTreeVO(Menu menu) {
        MenuTreeVO vo = new MenuTreeVO();
        vo.setMenuId(menu.getMenuId());
        vo.setParentId(menu.getParentId());
        vo.setMenuName(menu.getMenuName());
        vo.setPath(menu.getRoutePath());
        vo.setComponent(menu.getComponentPath());
        vo.setIcon(menu.getMenuIcon());
        vo.setType(menu.getMenuType());
        vo.setPermission(menu.getMenuPerm());
        vo.setSort(menu.getMenuSort());
        vo.setHidden(menu.getStatus() != null && menu.getStatus() == 0 ? 1 : 0);
        vo.setRedirect(null);
        vo.setStatus(menu.getStatus());
        vo.setCreateTime(menu.getCreateTime());
        vo.setUpdateTime(menu.getUpdateTime());
        return vo;
    }
}
