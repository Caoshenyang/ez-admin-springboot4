package com.ez.admin.common.framework.datascope;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.modules.system.entity.SysDept;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysRoleDeptRelation;
import com.ez.admin.modules.system.entity.SysUser;
import com.ez.admin.modules.system.mapper.SysRoleDeptRelationMapper;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据权限服务
 * <p>
 * 负责获取和计算用户的数据权限信息
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataScopeService {

    private final SysRoleMapper roleMapper;
    private final SysRoleDeptRelationMapper roleDeptRelationMapper;
    private final com.ez.admin.modules.system.mapper.SysDeptMapper deptMapper;

    /**
     * 获取用户的数据权限信息
     * <p>
     * 数据权限规则：
     * <ul>
     *   <li>如果用户有"超级管理员"角色，数据权限范围为【全部数据】</li>
     *   <li>否则，取用户所有角色中最大的数据权限范围</li>
     *   <li>如果最大数据权限范围为【自定义】，合并所有角色的自定义部门ID列表</li>
     * </ul>
     * </p>
     *
     * @param user 用户信息
     * @return 数据权限信息
     */
    public DataScopeInfo getDataScopeInfo(SysUser user) {
        // 1. 查询用户的所有角色
        List<Long> roleIds = roleMapper.selectRoleIdsByUserId(user.getUserId());
        if (roleIds == null || roleIds.isEmpty()) {
            log.warn("用户 {} 没有分配角色，默认使用【仅本人数据】权限", user.getUsername());
            return buildDataScopeInfo(SystemConstants.DATA_SCOPE_SELF, user, null);
        }

        // 2. 查询角色详情
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        if (roles == null || roles.isEmpty()) {
            log.warn("用户 {} 的角色查询失败，默认使用【仅本人数据】权限", user.getUsername());
            return buildDataScopeInfo(SystemConstants.DATA_SCOPE_SELF, user, null);
        }

        // 3. 检查是否有超级管理员角色
        boolean hasSuperAdmin = roles.stream()
                .anyMatch(role -> SystemConstants.ROLE_LABEL_SUPER_ADMIN.equals(role.getRoleLabel()));

        if (hasSuperAdmin) {
            log.info("用户 {} 拥有超级管理员角色，数据权限范围为【全部数据】", user.getUsername());
            return buildDataScopeInfo(SystemConstants.DATA_SCOPE_ALL, user, null);
        }

        // 4. 计算最大数据权限范围
        Integer maxDataScope = roles.stream()
                .map(SysRole::getDataScope)
                .max(Integer::compareTo)
                .orElse(SystemConstants.DATA_SCOPE_SELF);

        // 5. 如果是自定义权限，查询所有角色的自定义部门ID列表
        List<Long> customDeptIds = null;
        if (maxDataScope.equals(SystemConstants.DATA_SCOPE_CUSTOM)) {
            customDeptIds = getCustomDeptIds(roleIds);
            log.info("用户 {} 的数据权限范围为【自定义】，部门ID列表: {}", user.getUsername(), customDeptIds);
        }

        log.info("用户 {} 的数据权限范围为: {}", user.getUsername(), getDataScopeName(maxDataScope));

        return buildDataScopeInfo(maxDataScope, user, customDeptIds);
    }

    /**
     * 构建数据权限信息
     *
     * @param dataScope     数据权限范围
     * @param user          用户信息
     * @param customDeptIds 自定义部门ID列表
     * @return 数据权限信息
     */
    private DataScopeInfo buildDataScopeInfo(Integer dataScope, SysUser user, List<Long> customDeptIds) {
        // 从部门表查询 ancestors 字段
        String ancestors = null;
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                ancestors = dept.getAncestors();
            }
        }

        return DataScopeInfo.builder()
                .dataScope(dataScope)
                .userId(user.getUserId())
                .deptId(user.getDeptId())
                .ancestors(ancestors)
                .customDeptIds(customDeptIds)
                .build();
    }

    /**
     * 获取自定义数据权限的部门ID列表
     * <p>
     * 查询指定角色列表关联的所有部门ID，并去重
     * </p>
     *
     * @param roleIds 角色ID列表
     * @return 部门ID列表
     */
    private List<Long> getCustomDeptIds(List<Long> roleIds) {
        List<SysRoleDeptRelation> relations = roleDeptRelationMapper.selectList(
                new LambdaQueryWrapper<SysRoleDeptRelation>()
                        .in(SysRoleDeptRelation::getRoleId, roleIds)
        );

        Set<Long> deptIds = relations.stream()
                .map(SysRoleDeptRelation::getDeptId)
                .collect(Collectors.toSet());

        return deptIds.stream().toList();
    }

    /**
     * 获取数据权限范围名称
     *
     * @param dataScope 数据权限范围
     * @return 范围名称
     */
    private String getDataScopeName(Integer dataScope) {
        return switch (dataScope) {
            case SystemConstants.DATA_SCOPE_SELF -> "仅本人数据";
            case SystemConstants.DATA_SCOPE_DEPT -> "本部门数据";
            case SystemConstants.DATA_SCOPE_DEPT_AND_CHILD -> "本部门及以下数据";
            case SystemConstants.DATA_SCOPE_CUSTOM -> "自定义数据";
            case SystemConstants.DATA_SCOPE_ALL -> "全部数据";
            default -> "未知";
        };
    }
}
