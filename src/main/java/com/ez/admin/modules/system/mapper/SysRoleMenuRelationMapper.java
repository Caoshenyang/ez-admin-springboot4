package com.ez.admin.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ez.admin.modules.system.entity.SysRoleMenuRelation;

import java.util.List;

/**
 * <p>
 * 角色菜单关联表 Mapper 接口
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
public interface SysRoleMenuRelationMapper extends BaseMapper<SysRoleMenuRelation> {

    /**
     * 批量删除角色菜单关联
     *
     * @param roleId 角色ID
     * @return 删除条数
     */
    default int deleteByRoleId(Long roleId) {
        return this.delete(new LambdaQueryWrapper<SysRoleMenuRelation>()
                .eq(SysRoleMenuRelation::getRoleId, roleId));
    }

    /**
     * 根据角色ID列表查询菜单ID列表
     *
     * @param roleIds 角色ID列表
     * @return 菜单ID列表
     */
    default List<Long> selectMenuIdsByRoleIds(List<Long> roleIds) {
        return this.selectList(new LambdaQueryWrapper<SysRoleMenuRelation>()
                        .in(SysRoleMenuRelation::getRoleId, roleIds))
                .stream()
                .map(SysRoleMenuRelation::getMenuId)
                .distinct()
                .toList();
    }

}
