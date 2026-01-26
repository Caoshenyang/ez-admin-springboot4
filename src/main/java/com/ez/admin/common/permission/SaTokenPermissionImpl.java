package com.ez.admin.common.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ez.admin.modules.system.entity.SysRoleMenuRelation;
import com.ez.admin.modules.system.mapper.SysRoleMenuRelationMapper;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证接口实现
 * <p>
 * 实现 StpInterface 接口，为 Sa-Token 提供用户的权限码和角色列表
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaTokenPermissionImpl implements StpInterface {

    private final SysRoleMenuRelationMapper roleMenuRelationMapper;
    private final SysRoleMapper roleMapper;

    /**
     * 返回指定账号所拥有的权限码集合
     *
     * @param loginId   账号id（即 userId）
     * @param loginType 账号类型（多账号体系时使用，此处暂未使用）
     * @return 权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        // 1. 查询用户的所有角色
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查询这些角色的所有菜单
        List<Long> menuIds = roleMenuRelationMapper.selectList(
                        new LambdaQueryWrapper<SysRoleMenuRelation>()
                                .in(SysRoleMenuRelation::getRoleId, roleIds))
                .stream()
                .map(SysRoleMenuRelation::getMenuId)
                .distinct()
                .collect(Collectors.toList());

        if (menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 查询菜单的权限标识（menu_perm 字段）
        List<String> permissions = roleMapper.selectMenuPermsByIds(menuIds);

        log.debug("用户权限查询，用户ID：{}，权限数量：{}", userId, permissions.size());
        return permissions;
    }

    /**
     * 返回指定账号所拥有的角色标识集合
     *
     * @param loginId   账号id（即 userId）
     * @param loginType 账号类型
     * @return 角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        // 查询用户的所有角色标识（role_label）
        List<String> roleLabels = roleMapper.selectRoleLabelsByUserId(userId);

        log.debug("用户角色查询，用户ID：{}，角色数量：{}", userId, roleLabels.size());
        return roleLabels;
    }

    /**
     * 获取用户的角色ID列表（辅助方法）
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    private List<Long> getUserRoleIds(Long userId) {
        // 这里需要注入 SysUserRoleRelationMapper，但由于循环依赖问题，
        // 我们通过 RoleMapper 来查询
        return roleMapper.selectRoleIdsByUserId(userId);
    }
}
