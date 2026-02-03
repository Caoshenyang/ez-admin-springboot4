package com.ez.admin.common.framework.permission;

import cn.dev33.satoken.stp.StpInterface;
import com.ez.admin.dto.system.menu.vo.MenuPermissionVO;
import com.ez.admin.dto.system.vo.RoleInfo;
import com.ez.admin.cache.RoleCacheService;
import com.ez.admin.cache.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证接口实现（优化版）
 * <p>
 * 实现 StpInterface 接口，为 Sa-Token 提供用户的权限码和角色列表
 * </p>
 * <p>
 * 优化点：
 * <ul>
 *   <li>从缓存中读取权限和角色，避免每次查询数据库</li>
 *   <li>缓存由 DataInitializer 在启动时预加载</li>
 *   <li>数据变更时自动刷新缓存（通过 UserService、RoleService）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaTokenPermissionImpl implements StpInterface {

    private final UserCacheService userCacheService;
    private final RoleCacheService roleCacheService;

    /**
     * 返回指定账号所拥有的权限码集合
     * <p>
     * 权限码来源：
     * <ol>
     *   <li>获取用户的所有角色标识</li>
     *   <li>根据角色标识从缓存获取对应的菜单权限</li>
     *   <li>提取菜单权限中的权限标识（menuPerm）</li>
     * </ol>
     * </p>
     *
     * @param loginId   账号id（即 userId）
     * @param loginType 账号类型（多账号体系时使用，此处暂未使用）
     * @return 权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 1. 获取用户的角色列表
        List<String> roleLabels = getRoleList(loginId, loginType);
        if (roleLabels.isEmpty()) {
            log.debug("用户无角色，返回空权限列表：userId={}", loginId);
            return Collections.emptyList();
        }

        // 2. 根据角色列表从缓存获取菜单权限
        List<MenuPermissionVO> allMenuPermissions = roleLabels.stream()
                .map(roleCacheService::getRoleMenuPermissions)
                .filter(permissions -> permissions != null && !permissions.isEmpty())
                .flatMap(List::stream)
                .collect(Collectors.toList());

        if (allMenuPermissions.isEmpty()) {
            log.debug("用户角色无权限，返回空权限列表：userId={}, roles={}", loginId, roleLabels);
            return Collections.emptyList();
        }

        // 3. 提取权限标识
        List<String> permissions = allMenuPermissions.stream()
                .map(MenuPermissionVO::getMenuPerm)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();

        log.debug("用户权限查询成功：userId={}, permissionsCount={}", loginId, permissions.size());
        return permissions;
    }

    /**
     * 返回指定账号所拥有的角色标识集合
     * <p>
     * 从缓存中获取用户的角色标识列表
     * </p>
     *
     * @param loginId   账号id（即 userId）
     * @param loginType 账号类型
     * @return 角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            log.warn("loginId 为空，返回空角色列表");
            return Collections.emptyList();
        }

        Long userId = parseUserId(loginId);
        if (userId == null) {
            log.warn("loginId 解析失败：{}", loginId);
            return Collections.emptyList();
        }

        // 从缓存获取用户角色
        List<RoleInfo> roleInfoList = userCacheService.getUserRoles(userId);
        if (roleInfoList == null) {
            log.debug("用户角色缓存为空：userId={}", userId);
            return Collections.emptyList();
        }

        List<String> roleLabels = roleInfoList.stream()
                .map(RoleInfo::getRoleLabel)
                .collect(Collectors.toList());

        log.debug("用户角色查询成功：userId={}, rolesCount={}", userId, roleLabels.size());
        return roleLabels;
    }

    /**
     * 解析用户ID
     * <p>
     * 支持类型：
     * <ul>
     *   <li>Long 类型</li>
     *   <li>Integer 类型</li>
     *   <li>String 类型（数字字符串）</li>
     * </ul>
     * </p>
     *
     * @param loginId 登录ID
     * @return 用户ID，解析失败返回 null
     */
    private Long parseUserId(Object loginId) {
        try {
            if (loginId instanceof Number number) {
                return number.longValue();
            }
            return Long.parseLong(String.valueOf(loginId));
        } catch (NumberFormatException e) {
            log.error("用户ID解析失败：loginId={}", loginId, e);
            return null;
        }
    }
}
