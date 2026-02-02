package com.ez.admin.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.data.mapstruct.MenuConverter;
import com.ez.admin.common.data.tree.TreeBuilder;
import com.ez.admin.dto.system.menu.vo.MenuTreeVO;
import com.ez.admin.modules.system.entity.SysMenu;
import com.ez.admin.modules.system.entity.SysRoleMenuRelation;
import com.ez.admin.modules.system.mapper.SysMenuMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单信息表 服务实现类
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Service
@RequiredArgsConstructor
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

    private final SysRoleMenuRelationService roleMenuRelationService;

    /**
     * 获取用户的菜单树（只包含目录和菜单，不包含按钮）
     *
     * @param roleIds 角色ID列表
     * @return 菜单树
     */
    public List<MenuTreeVO> getUserMenuTree(List<Long> roleIds) {
        // 0. 角色ID列表为空时，直接返回空结果
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        // 1. 查询角色拥有的菜单ID列表
        LambdaQueryWrapper<SysRoleMenuRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenuRelation::getRoleId, roleIds);
        List<Long> menuIds = roleMenuRelationService.list(wrapper)
                .stream()
                .map(SysRoleMenuRelation::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return List.of();
        }

        // 2. 查询菜单信息（只查询目录和菜单，不查询按钮）
        List<SysMenu> menuList = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .in(SysMenu::getMenuId, menuIds)
                .in(SysMenu::getMenuType, 1, 2)  // 1=目录 2=菜单
                .eq(SysMenu::getStatus, SystemConstants.STATUS_NORMAL)
                .eq(SysMenu::getIsDeleted, SystemConstants.NOT_DELETED)
                .orderByAsc(SysMenu::getMenuSort));

        // 3. 转换为 VO
        List<MenuTreeVO> menuVOList = MenuConverter.INSTANCE.toTreeVOList(menuList);

        // 4. 构建树形结构
        return TreeBuilder.of(menuVOList)
                .enableSort()
                .build();
    }

    /**
     * 获取用户的所有权限标识（包括按钮权限）
     *
     * @param roleIds 角色ID列表
     * @return 权限标识列表
     */
    public List<String> getUserPermissions(List<Long> roleIds) {
        // 0. 角色ID列表为空时，直接返回空结果
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        // 1. 查询角色拥有的菜单ID列表
        LambdaQueryWrapper<SysRoleMenuRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenuRelation::getRoleId, roleIds);
        List<Long> menuIds = roleMenuRelationService.list(wrapper)
                .stream()
                .map(SysRoleMenuRelation::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return List.of();
        }

        // 2. 查询所有菜单的权限标识（包括按钮）
        return baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getMenuId, menuIds)
                        .eq(SysMenu::getStatus, SystemConstants.STATUS_NORMAL)
                        .eq(SysMenu::getIsDeleted, SystemConstants.NOT_DELETED)
                        .isNotNull(SysMenu::getMenuPerm))
                .stream()
                .map(SysMenu::getMenuPerm)
                .filter(perm -> perm != null && !perm.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }
}
