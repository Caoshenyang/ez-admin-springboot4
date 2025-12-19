package com.ezadmin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ezadmin.common.response.page.PageQuery;
import com.ezadmin.common.response.page.PageVO;
import com.ezadmin.model.dto.RoleCreateDTO;
import com.ezadmin.model.dto.RoleUpdateDTO;
import com.ezadmin.model.query.RoleQuery;
import com.ezadmin.model.vo.RoleDetailVO;
import com.ezadmin.modules.system.entity.Role;
import com.ezadmin.modules.system.entity.RoleDeptRelation;
import com.ezadmin.modules.system.entity.RoleMenuRelation;
import com.ezadmin.modules.system.service.IRoleDeptRelationService;
import com.ezadmin.modules.system.service.IRoleMenuRelationService;
import com.ezadmin.modules.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理业务
 */
@Service
@RequiredArgsConstructor
public class RoleManagementService {

    private final IRoleService roleService;
    private final IRoleMenuRelationService roleMenuRelationService;
    private final IRoleDeptRelationService roleDeptRelationService;

    /**
     * 分页查询角色
     */
    public PageVO<Role> page(PageQuery<RoleQuery> query) {
        Page<Role> page = query.toMpPage();
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        RoleQuery search = query.getSearch();
        if (search != null) {
            wrapper = search.buildWrapper();
            if (search.getStatus() != null) {
                wrapper.eq(Role::getStatus, search.getStatus());
            }
        }
        roleService.page(page, wrapper);
        return PageVO.of(page, Role.class);
    }

    /**
     * 角色详情，带菜单/数据权限
     */
    public RoleDetailVO detail(Long roleId) {
        Role role = roleService.getById(roleId);
        if (role == null) {
            return null;
        }
        RoleDetailVO vo = BeanUtil.copyProperties(role, RoleDetailVO.class);
        vo.setMenuIds(loadMenuIds(roleId));
        vo.setDeptIds(loadDeptIds(roleId));
        return vo;
    }

    /**
     * 新增角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleCreateDTO dto) {
        Role role = BeanUtil.copyProperties(dto, Role.class);
        roleService.save(role);
        saveRelations(role.getRoleId(), dto.getMenuIds(), dto.getDeptIds(), dto.getDataScope());
    }

    /**
     * 更新角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleUpdateDTO dto) {
        Role role = BeanUtil.copyProperties(dto, Role.class);
        roleService.updateById(role);
        saveRelations(dto.getRoleId(), dto.getMenuIds(), dto.getDeptIds(), dto.getDataScope());
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long roleId) {
        roleService.removeById(roleId);
        cleanupRelations(roleId);
    }

    /**
     * 查询全部角色（用于下拉）
     */
    public List<Role> listAll() {
        return roleService.lambdaQuery().orderByAsc(Role::getRoleSort).list();
    }

    private void saveRelations(Long roleId, List<Long> menuIds, List<Long> deptIds, Integer dataScope) {
        cleanupRelations(roleId);
        if (!CollectionUtils.isEmpty(menuIds)) {
            List<RoleMenuRelation> relations = new ArrayList<>();
            for (Long menuId : menuIds) {
                RoleMenuRelation relation = new RoleMenuRelation();
                relation.setRoleId(roleId);
                relation.setMenuId(menuId);
                relations.add(relation);
            }
            roleMenuRelationService.saveBatch(relations);
        }
        if (dataScope != null && dataScope == 4 && !CollectionUtils.isEmpty(deptIds)) {
            List<RoleDeptRelation> deptRelations = new ArrayList<>();
            for (Long deptId : deptIds) {
                RoleDeptRelation relation = new RoleDeptRelation();
                relation.setRoleId(roleId);
                relation.setDeptId(deptId);
                deptRelations.add(relation);
            }
            roleDeptRelationService.saveBatch(deptRelations);
        }
    }

    private void cleanupRelations(Long roleId) {
        roleMenuRelationService.remove(new LambdaQueryWrapper<RoleMenuRelation>()
            .eq(RoleMenuRelation::getRoleId, roleId));
        roleDeptRelationService.remove(new LambdaQueryWrapper<RoleDeptRelation>()
            .eq(RoleDeptRelation::getRoleId, roleId));
    }

    private List<Long> loadMenuIds(Long roleId) {
        return roleMenuRelationService.lambdaQuery()
            .eq(RoleMenuRelation::getRoleId, roleId)
            .list()
            .stream()
            .map(RoleMenuRelation::getMenuId)
            .toList();
    }

    private List<Long> loadDeptIds(Long roleId) {
        return roleDeptRelationService.lambdaQuery()
            .eq(RoleDeptRelation::getRoleId, roleId)
            .list()
            .stream()
            .map(RoleDeptRelation::getDeptId)
            .toList();
    }
}
