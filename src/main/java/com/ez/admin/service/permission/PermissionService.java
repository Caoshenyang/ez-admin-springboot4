package com.ez.admin.service.permission;

import com.ez.admin.common.infrastructure.cache.AdminCache;
import com.ez.admin.dto.system.menu.vo.MenuPermissionVO;
import com.ez.admin.dto.system.vo.SuperAdminPermissionSyncVO;
import com.ez.admin.common.core.constant.SystemConstants;
import com.ez.admin.common.core.exception.EzBusinessException;
import com.ez.admin.common.core.exception.ErrorCode;
import com.ez.admin.modules.system.entity.SysMenu;
import com.ez.admin.modules.system.entity.SysRole;
import com.ez.admin.modules.system.entity.SysRoleMenuRelation;
import com.ez.admin.modules.system.mapper.SysMenuMapper;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import com.ez.admin.modules.system.mapper.SysRoleMenuRelationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 权限管理服务
 * <p>
 * 统一管理所有权限相关的业务逻辑，包括：
 * <ul>
 *   <li>超级管理员权限同步</li>
 *   <li>角色菜单权限缓存初始化</li>
 *   <li>路由权限缓存刷新</li>
 *   <li>用户/角色缓存失效管理</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final AdminCache adminCache;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysRoleMenuRelationMapper roleMenuRelationMapper;
    private final com.ez.admin.modules.system.service.SysRoleMenuRelationService roleMenuRelationService;

    // ============================= 超级管理员权限管理 =============================

    /**
     * 同步超级管理员权限
     * <p>
     * 将所有启用状态的菜单权限分配给 SUPER_ADMIN 角色，并刷新缓存
     * </p>
     *
     * @return 同步结果对象
     * @throws EzBusinessException 当 SUPER_ADMIN 角色不存在时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public SuperAdminPermissionSyncVO syncSuperAdminPermissions() {
        log.debug("开始同步超级管理员权限...");

        // 1. 查询 SUPER_ADMIN 角色
        SysRole superAdminRole = roleMapper.selectByRoleLabel(SystemConstants.ROLE_LABEL_SUPER_ADMIN);
        if (superAdminRole == null) {
            throw new EzBusinessException(ErrorCode.ROLE_NOT_FOUND, "SUPER_ADMIN 角色不存在");
        }

        // 2. 查询所有启用状态的菜单
        List<SysMenu> allMenus = menuMapper.selectAllActiveMenus();
        if (allMenus.isEmpty()) {
            log.warn("系统中暂无菜单");
            return SuperAdminPermissionSyncVO.builder()
                    .menuCount(0)
                    .roleLabel(superAdminRole.getRoleLabel())
                    .build();
        }

        // 3. 删除并重新建立菜单关联（批量插入）
        roleMenuRelationMapper.deleteByRoleId(superAdminRole.getRoleId());

        // 构建批量插入数据
        List<SysRoleMenuRelation> relations = allMenus.stream()
                .map(menu -> {
                    SysRoleMenuRelation relation = new SysRoleMenuRelation();
                    relation.setRoleId(superAdminRole.getRoleId());
                    relation.setMenuId(menu.getMenuId());
                    return relation;
                })
                .collect(Collectors.toList());

        // 使用 MyBatis-Plus 批量插入（IService.saveBatch 使用 JDBC Batch 优化）
        if (!relations.isEmpty()) {
            roleMenuRelationService.saveBatch(relations);
        }

        // 4. 刷新缓存
        List<MenuPermissionVO> menuPermissions = convertToMenuPermissions(allMenus, superAdminRole.getRoleId());
        adminCache.cacheRoleMenuPermissions(superAdminRole.getRoleLabel(), menuPermissions);

        log.info("超级管理员权限同步成功，角色ID：{}，菜单数量：{}", superAdminRole.getRoleId(), allMenus.size());

        return SuperAdminPermissionSyncVO.builder()
                .menuCount(allMenus.size())
                .roleLabel(superAdminRole.getRoleLabel())
                .build();
    }

    // ============================= 角色菜单权限管理 =============================

    /**
     * 初始化所有角色的菜单权限缓存
     * <p>
     * 从数据库加载所有角色的菜单权限，预热到 Redis 缓存
     * </p>
     *
     * @return 成功缓存的角色数量
     */
    public int initAllRoleMenuPermissions() {
        log.debug("开始初始化所有角色的菜单权限缓存...");

        // 1. 查询所有角色
        List<SysRole> allRoles = roleMapper.selectList(null);
        if (allRoles.isEmpty()) {
            log.warn("未找到任何角色，跳过权限缓存初始化");
            return 0;
        }

        log.debug("共 {} 个角色需要预加载权限", allRoles.size());

        // 2. 批量加载菜单权限
        List<Long> roleIds = allRoles.stream()
                .map(SysRole::getRoleId)
                .collect(Collectors.toList());

        Map<Long, List<MenuPermissionVO>> permissionMap = loadMenuPermissionsByRoleIds(roleIds);

        // 3. 写入 Redis 缓存
        int cachedCount = 0;
        for (SysRole role : allRoles) {
            List<MenuPermissionVO> permissions = permissionMap.get(role.getRoleId());
            if (permissions != null && !permissions.isEmpty()) {
                adminCache.cacheRoleMenuPermissions(role.getRoleLabel(), permissions);
                cachedCount++;
            }
        }

        log.info("角色菜单权限缓存初始化完成：成功 {} 个", cachedCount);
        return cachedCount;
    }

    /**
     * 根据角色ID列表批量加载菜单权限
     *
     * @param roleIds 角色ID列表
     * @return 角色ID -> 菜单权限列表的映射
     */
    private Map<Long, List<MenuPermissionVO>> loadMenuPermissionsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Map.of();
        }

        try {
            // 1. 提取菜单ID列表
            List<Long> menuIds = roleMenuRelationMapper.selectMenuIdsByRoleIds(roleIds);
            if (menuIds.isEmpty()) {
                return Map.of();
            }

            // 2. 查询菜单权限信息
            List<SysMenu> menus = menuMapper.selectActiveMenusByIds(menuIds);
            Map<Long, SysMenu> menuMap = menus.stream()
                    .collect(Collectors.toMap(SysMenu::getMenuId, m -> m));

            // 3. 查询角色-菜单关联关系
            List<SysRoleMenuRelation> relations = roleMenuRelationMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRoleMenuRelation>()
                            .in(SysRoleMenuRelation::getRoleId, roleIds)
            );

            // 4. 构建角色ID -> 权限列表的映射
            return relations.stream()
                    .map(relation -> {
                        SysMenu menu = menuMap.get(relation.getMenuId());
                        if (menu == null || menu.getMenuPerm() == null || menu.getMenuPerm().isEmpty()) {
                            return null;
                        }
                        return Map.entry(
                                relation.getRoleId(),
                                MenuPermissionVO.builder()
                                        .menuId(menu.getMenuId())
                                        .menuPerm(menu.getMenuPerm())
                                        .menuType(menu.getMenuType())
                                        .roleId(relation.getRoleId())
                                        .build()
                        );
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                    ));

        } catch (Exception e) {
            log.error("加载角色菜单权限失败：roleIds={}", roleIds, e);
            return Map.of();
        }
    }

    /**
     * 刷新指定角色的菜单权限缓存
     * <p>
     * 用于角色分配权限后，刷新该角色的缓存
     * </p>
     *
     * @param roleId 角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshRoleMenuPermissions(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null) {
            log.warn("角色不存在，跳过缓存刷新：roleId={}", roleId);
            return;
        }

        Map<Long, List<MenuPermissionVO>> permissionMap = loadMenuPermissionsByRoleIds(List.of(roleId));
        List<MenuPermissionVO> permissions = permissionMap.get(roleId);

        if (permissions != null && !permissions.isEmpty()) {
            adminCache.cacheRoleMenuPermissions(role.getRoleLabel(), permissions);
            log.info("角色菜单权限缓存刷新成功：roleId={}, roleLabel={}, permissions={}",
                    roleId, role.getRoleLabel(), permissions.size());
        } else {
            // 角色无权限时，清除缓存
            adminCache.evictRoleMenuPermissions(role.getRoleLabel());
            log.info("角色无权限，已清除缓存：roleId={}, roleLabel={}", roleId, role.getRoleLabel());
        }
    }

    // ============================= 用户角色缓存管理 =============================

    /**
     * 刷新指定用户的角色缓存
     * <p>
     * 用于用户角色分配后，刷新该用户的缓存
     * </p>
     *
     * @param userId 用户ID
     */
    public void refreshUserRoles(Long userId) {
        List<String> roleLabels = roleMapper.selectRoleLabelsByUserId(userId);
        adminCache.cacheUserRoles(userId, roleLabels);
        log.info("用户角色缓存刷新成功：userId={}, roles={}", userId, roleLabels.size());
    }

    /**
     * 清除指定用户的角色缓存
     *
     * @param userId 用户ID
     */
    public void evictUserRoles(Long userId) {
        adminCache.evictUserRoles(userId);
        log.debug("用户角色缓存已清除：userId={}", userId);
    }

    /**
     * 清除指定角色的菜单权限缓存
     *
     * @param roleLabel 角色标识
     */
    public void evictRoleMenuPermissions(String roleLabel) {
        adminCache.evictRoleMenuPermissions(roleLabel);
        log.debug("角色菜单权限缓存已清除：roleLabel={}", roleLabel);
    }

    // ============================= 路由权限缓存管理 =============================

    /**
     * 刷新路由权限缓存
     * <p>
     * 从数据库重新加载所有路由权限规则到 Redis
     * </p>
     *
     * @return 加载的路由权限规则数量
     */
    public int refreshRoutePermissions() {
        log.debug("开始刷新路由权限缓存...");

        try {
            // 1. 从数据库查询所有启用状态的菜单
            List<SysMenu> menuList = menuMapper.selectAllActiveMenus();

            // 2. 构建路由权限映射
            Map<String, String> routePermMap = new java.util.HashMap<>();
            for (SysMenu menu : menuList) {
                String route = menu.getApiRoute();
                String method = menu.getApiMethod();
                String perm = menu.getMenuPerm();

                if (route != null && !route.isEmpty()) {
                    String routeKey = buildRouteKey(route, method);
                    routePermMap.put(routeKey, perm != null ? perm : "");
                }
            }

            // 3. 刷新到 Redis（通过 AdminCache）
            adminCache.refreshRoutePermissionCache(routePermMap);

            log.info("路由权限缓存刷新成功，共 {} 条规则", routePermMap.size());
            return routePermMap.size();

        } catch (Exception e) {
            log.error("路由权限缓存刷新失败", e);
            throw new RuntimeException("路由权限缓存刷新失败", e);
        }
    }

    /**
     * 构建路由键
     * <p>
     * 格式：METHOD:PATH，如 "POST:/api/user"
     * </p>
     *
     * @param route  API 路由（如 /api/user）
     * @param method HTTP 方法（如 POST）
     * @return 路由键
     */
    private String buildRouteKey(String route, String method) {
        if (method != null && !method.isEmpty()) {
            return method + ":" + route;
        }
        return route;
    }

    // ============================= 辅助方法 =============================

    /**
     * 转换菜单列表为菜单权限VO列表
     *
     * @param menus  菜单列表
     * @param roleId 角色ID
     * @return 菜单权限VO列表
     */
    private List<MenuPermissionVO> convertToMenuPermissions(List<SysMenu> menus, Long roleId) {
        return menus.stream()
                .filter(menu -> menu.getMenuPerm() != null && !menu.getMenuPerm().isEmpty())
                .map(menu -> MenuPermissionVO.builder()
                        .menuId(menu.getMenuId())
                        .menuPerm(menu.getMenuPerm())
                        .menuType(menu.getMenuType())
                        .roleId(roleId)
                        .build())
                .collect(Collectors.toList());
    }
}
