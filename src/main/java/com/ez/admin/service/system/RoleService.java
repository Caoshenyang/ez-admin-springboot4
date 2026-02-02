package com.ez.admin.service.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.common.data.mapstruct.RoleConverter;
import com.ez.admin.common.model.model.PageQuery;
import com.ez.admin.common.model.model.PageVO;
import com.ez.admin.dto.system.role.req.RoleCreateReq;
import com.ez.admin.dto.system.role.req.RoleStatusChangeReq;
import com.ez.admin.dto.system.role.req.RoleUpdateReq;
import com.ez.admin.dto.system.role.vo.RoleDetailVO;
import com.ez.admin.dto.system.role.vo.RoleListVO;
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
    private final com.ez.admin.service.permission.PermissionService permissionService;
    private final com.ez.admin.modules.system.service.SysRoleMenuRelationService roleMenuRelationService;
    private final com.ez.admin.modules.system.service.SysRoleDeptRelationService roleDeptRelationService;

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

        // 删除角色菜单权限缓存
        permissionService.evictRoleMenuPermissions(role.getRoleLabel());

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

        // 批量删除角色菜单权限缓存
        List<String> roleLabels = roleMapper.selectList(
                        new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, roleIds))
                .stream()
                .map(SysRole::getRoleLabel)
                .toList();
        for (String roleLabel : roleLabels) {
            permissionService.evictRoleMenuPermissions(roleLabel);
        }

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

    /**
     * 分配菜单权限（覆盖式）
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 1. 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 删除原有菜单关联
        roleMenuRelationMapper.delete(new LambdaQueryWrapper<SysRoleMenuRelation>()
                .eq(SysRoleMenuRelation::getRoleId, roleId));

        // 3. 批量插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenuRelation> relations = menuIds.stream()
                    .map(menuId -> {
                        SysRoleMenuRelation relation = new SysRoleMenuRelation();
                        relation.setRoleId(roleId);
                        relation.setMenuId(menuId);
                        return relation;
                    })
                    .toList();

            // 批量插入（使用 MyBatis-Plus IService.saveBatch，底层 JDBC Batch 优化）
            roleMenuRelationService.saveBatch(relations);

            // 刷新角色菜单权限缓存
            permissionService.refreshRoleMenuPermissions(roleId);
        }

        log.info("角色分配菜单成功，角色ID：{}，菜单数量：{}", roleId, menuIds != null ? menuIds.size() : 0);
    }

    /**
     * 获取角色菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuRelationMapper.selectList(new LambdaQueryWrapper<SysRoleMenuRelation>()
                        .eq(SysRoleMenuRelation::getRoleId, roleId))
                .stream()
                .map(SysRoleMenuRelation::getMenuId)
                .collect(Collectors.toList());
    }

    /**
     * 分配数据权限（部门，覆盖式）
     *
     * @param roleId  角色ID
     * @param deptIds 部门ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignDepts(Long roleId, List<Long> deptIds) {
        // 1. 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 删除原有部门关联
        roleDeptRelationMapper.delete(new LambdaQueryWrapper<SysRoleDeptRelation>()
                .eq(SysRoleDeptRelation::getRoleId, roleId));

        // 3. 批量插入新关联
        if (deptIds != null && !deptIds.isEmpty()) {
            List<SysRoleDeptRelation> relations = deptIds.stream()
                    .map(deptId -> {
                        SysRoleDeptRelation relation = new SysRoleDeptRelation();
                        relation.setRoleId(roleId);
                        relation.setDeptId(deptId);
                        return relation;
                    })
                    .collect(Collectors.toList());

            // 批量插入（使用 MyBatis-Plus IService.saveBatch，底层 JDBC Batch 优化）
            roleDeptRelationService.saveBatch(relations);
        }

        log.info("角色分配部门成功，角色ID：{}，部门数量：{}", roleId, deptIds != null ? deptIds.size() : 0);
    }

    /**
     * 获取角色部门ID列表
     *
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    public List<Long> getRoleDeptIds(Long roleId) {
        return roleDeptRelationMapper.selectList(new LambdaQueryWrapper<SysRoleDeptRelation>()
                        .eq(SysRoleDeptRelation::getRoleId, roleId))
                .stream()
                .map(SysRoleDeptRelation::getDeptId)
                .collect(Collectors.toList());
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
     * 切换角色状态
     *
     * @param request 状态切换请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(RoleStatusChangeReq request) {
        // 1. 检查角色是否存在
        SysRole role = roleMapper.selectById(request.getRoleId());
        if (role == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 2. 更新角色状态
        SysRole updateRole = new SysRole();
        updateRole.setRoleId(request.getRoleId());
        updateRole.setStatus(request.getStatus());
        roleMapper.updateById(updateRole);

        // 3. 如果禁用角色，清除相关缓存
        if (request.getStatus() == 0) {
            permissionService.evictRoleMenuPermissions(role.getRoleLabel());
        }

        log.info("角色状态切换成功，角色ID：{}，状态：{}", request.getRoleId(), request.getStatus());
    }

    /**
     * 批量分配菜单（多个角色分配相同菜单）
     *
     * @param roleIds 角色ID列表
     * @param menuIds 菜单ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchAssignMenus(List<Long> roleIds, List<Long> menuIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "角色ID列表不能为空");
        }
        if (menuIds == null || menuIds.isEmpty()) {
            throw new EzBusinessException(ErrorCode.VALIDATION_ERROR, "菜单ID列表不能为空");
        }

        // 批量为每个角色分配菜单
        for (Long roleId : roleIds) {
            assignMenus(roleId, menuIds);
        }

        log.info("批量分配菜单成功，角色数量：{}，菜单数量：{}", roleIds.size(), menuIds.size());
    }
}
