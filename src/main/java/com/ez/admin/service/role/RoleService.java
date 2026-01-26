package com.ez.admin.service.role;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.exception.EzBusinessException;
import com.ez.admin.common.exception.ErrorCode;
import com.ez.admin.common.mapstruct.RoleConverter;
import com.ez.admin.common.model.PageQuery;
import com.ez.admin.common.model.PageVO;
import com.ez.admin.dto.role.req.RoleCreateReq;
import com.ez.admin.dto.role.req.RoleUpdateReq;
import com.ez.admin.dto.role.vo.RoleDetailVO;
import com.ez.admin.dto.role.vo.RoleListVO;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysRoleDeptRelation;
import com.ez.admin.modules.system.entity.SysRoleMenuRelation;
import com.ez.admin.modules.system.mapper.SysRoleDeptRelationMapper;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import com.ez.admin.modules.system.mapper.SysRoleMenuRelationMapper;
import com.ez.admin.modules.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务
 * <p>
 * 业务聚合层，组合原子服务实现角色管理的复杂业务逻辑
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuRelationMapper roleMenuRelationMapper;
    private final SysRoleDeptRelationMapper roleDeptRelationMapper;
    private final SysRoleService sysRoleService;
    private final RoleConverter roleConverter;

    /**
     * 创建角色
     *
     * @param request 创建请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void createRole(RoleCreateReq request) {
        // 1. 检查角色名称是否已存在
        if (roleMapper.existsByRoleName(request.getRoleName())) {
            throw new EzBusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        // 2. 检查角色标识是否已存在
        if (StringUtils.hasText(request.getRoleLabel()) && roleMapper.existsByRoleLabel(request.getRoleLabel())) {
            throw new EzBusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        // 3. 创建角色
        SysRole role = buildRole(request);
        roleMapper.insert(role);

        // 4. 分配菜单权限
        if (request.getMenuIds() != null && !request.getMenuIds().isEmpty()) {
            assignMenus(role.getRoleId(), request.getMenuIds());
        }

        // 5. 分配数据权限（部门）
        if (request.getDeptIds() != null && !request.getDeptIds().isEmpty()) {
            assignDepts(role.getRoleId(), request.getDeptIds());
        }

        log.info("创建角色成功，角色名称：{}", request.getRoleName());
    }

    /**
     * 更新角色
     *
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateReq request) {
        // 1. 检查角色是否存在
        SysRole existRole = roleMapper.selectById(request.getRoleId());
        if (existRole == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 检查角色名称是否被其他角色占用
        if (roleMapper.existsByRoleNameExclude(request.getRoleName(), request.getRoleId())) {
            throw new EzBusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        // 3. 检查角色标识是否被其他角色占用
        if (StringUtils.hasText(request.getRoleLabel()) && roleMapper.existsByRoleLabelExclude(request.getRoleLabel(), request.getRoleId())) {
            throw new EzBusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        // 4. 更新角色信息
        SysRole role = new SysRole();
        role.setRoleId(request.getRoleId());
        role.setRoleName(request.getRoleName());
        role.setRoleLabel(request.getRoleLabel());
        role.setRoleSort(request.getRoleSort());
        role.setDataScope(request.getDataScope());
        role.setStatus(request.getStatus());
        role.setDescription(request.getDescription());
        roleMapper.updateById(role);

        // 5. 重新分配菜单权限
        if (request.getMenuIds() != null) {
            // 先删除原有菜单关联
            roleMenuRelationMapper.delete(new LambdaQueryWrapper<SysRoleMenuRelation>()
                    .eq(SysRoleMenuRelation::getRoleId, request.getRoleId()));
            // 分配新菜单
            assignMenus(request.getRoleId(), request.getMenuIds());
        }

        // 6. 重新分配数据权限（部门）
        if (request.getDeptIds() != null) {
            // 先删除原有部门关联
            roleDeptRelationMapper.delete(new LambdaQueryWrapper<SysRoleDeptRelation>()
                    .eq(SysRoleDeptRelation::getRoleId, request.getRoleId()));
            // 分配新部门
            assignDepts(request.getRoleId(), request.getDeptIds());
        }

        log.info("更新角色成功，角色ID：{}", request.getRoleId());
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 1. 检查角色是否存在
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 检查角色是否已分配给用户
        if (roleMapper.isAssignedToUsers(roleId)) {
            throw new EzBusinessException(ErrorCode.ROLE_ASSIGNED_TO_USER);
        }

        // 3. 逻辑删除角色
        sysRoleService.removeById(roleId);

        // 4. 删除角色菜单关联
        roleMenuRelationMapper.delete(new LambdaQueryWrapper<SysRoleMenuRelation>()
                .eq(SysRoleMenuRelation::getRoleId, roleId));

        // 5. 删除角色部门关联
        roleDeptRelationMapper.delete(new LambdaQueryWrapper<SysRoleDeptRelation>()
                .eq(SysRoleDeptRelation::getRoleId, roleId));

        log.info("删除角色成功，角色ID：{}", roleId);
    }

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR);
        }

        // 批量逻辑删除角色
        sysRoleService.removeByIds(roleIds);

        // 批量删除角色菜单关联
        roleMenuRelationMapper.delete(new LambdaQueryWrapper<SysRoleMenuRelation>()
                .in(SysRoleMenuRelation::getRoleId, roleIds));

        // 批量删除角色部门关联
        roleDeptRelationMapper.delete(new LambdaQueryWrapper<SysRoleDeptRelation>()
                .in(SysRoleDeptRelation::getRoleId, roleIds));

        log.info("批量删除角色成功，数量：{}", roleIds.size());
    }

    /**
     * 根据ID查询角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    public RoleDetailVO getRoleById(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 查询角色菜单ID列表
        List<Long> menuIds = getRoleMenuIds(roleId);

        // 查询角色部门ID列表
        List<Long> deptIds = getRoleDeptIds(roleId);

        // 使用 MapStruct 转换
        return roleConverter.toDetailVOWithMenusAndDepts(role, menuIds, deptIds);
    }

    /**
     * 分页查询角色列表
     *
     * @param query 分页查询请求
     * @return 分页结果
     */
    public PageVO<RoleListVO> getRolePage(PageQuery query) {
        // 执行分页查询
        Page<SysRole> result = roleMapper.selectRolePage(query.toMpPage(), query);

        return PageVO.of(result, roleConverter::toListVO);
    }

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    public List<RoleListVO> getRoleList() {
        List<SysRole> roles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getRoleSort));
        return roleConverter.toListVOList(roles);
    }

    // ==================== 私有方法 ====================

    /**
     * 构建角色实体
     */
    private SysRole buildRole(RoleCreateReq request) {
        SysRole role = new SysRole();
        role.setRoleName(request.getRoleName());
        role.setRoleLabel(request.getRoleLabel());
        role.setRoleSort(request.getRoleSort());
        role.setDataScope(request.getDataScope());
        role.setStatus(request.getStatus());
        role.setDescription(request.getDescription());
        return role;
    }

    /**
     * 分配菜单权限
     */
    private void assignMenus(Long roleId, List<Long> menuIds) {
        List<SysRoleMenuRelation> relations = menuIds.stream()
                .map(menuId -> {
                    SysRoleMenuRelation relation = new SysRoleMenuRelation();
                    relation.setRoleId(roleId);
                    relation.setMenuId(menuId);
                    return relation;
                })
                .collect(Collectors.toList());

        // 批量插入
        relations.forEach(roleMenuRelationMapper::insert);
    }

    /**
     * 分配数据权限（部门）
     */
    private void assignDepts(Long roleId, List<Long> deptIds) {
        List<SysRoleDeptRelation> relations = deptIds.stream()
                .map(deptId -> {
                    SysRoleDeptRelation relation = new SysRoleDeptRelation();
                    relation.setRoleId(roleId);
                    relation.setDeptId(deptId);
                    return relation;
                })
                .collect(Collectors.toList());

        // 批量插入
        relations.forEach(roleDeptRelationMapper::insert);
    }

    /**
     * 获取角色菜单ID列表
     */
    private List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuRelationMapper.selectList(new LambdaQueryWrapper<SysRoleMenuRelation>()
                        .eq(SysRoleMenuRelation::getRoleId, roleId))
                .stream()
                .map(SysRoleMenuRelation::getMenuId)
                .collect(Collectors.toList());
    }

    /**
     * 获取角色部门ID列表
     */
    private List<Long> getRoleDeptIds(Long roleId) {
        return roleDeptRelationMapper.selectList(new LambdaQueryWrapper<SysRoleDeptRelation>()
                        .eq(SysRoleDeptRelation::getRoleId, roleId))
                .stream()
                .map(SysRoleDeptRelation::getDeptId)
                .collect(Collectors.toList());
    }
}
