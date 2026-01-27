package com.ez.admin.common.infrastructure.cache;

import com.ez.admin.common.infrastructure.redis.RedisCache;
import com.ez.admin.dto.menu.vo.MenuPermissionVO;
import com.ez.admin.modules.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 管理系统业务缓存
 * <p>
 * 提供高层次的业务缓存操作，封装 Redis 缓存细节
 * </p>
 * <p>
 * 缓存 TTL 策略：
 * <ul>
 *   <li>用户角色：1 天（变更频率中等）</li>
 *   <li>角色菜单权限：30 天（变更频率低）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminCache {

    private final RedisCache redisCache;
    private final SysRoleMapper roleMapper;

    // ============================= 缓存 TTL 配置 =============================

    /**
     * 用户角色缓存过期时间：1 天（秒）
     */
    private static final int USER_ROLES_TTL = 24 * 60 * 60;

    /**
     * 角色菜单权限缓存过期时间：30 天（秒）
     */
    private static final int ROLE_MENUS_TTL = 30 * 24 * 60 * 60;

    // ============================= 缓存 Key 定义 =============================

    /**
     * 用户角色缓存 Key 模式：user:roles:{userId}
     */
    private static final String USER_ROLES_KEY = "user:roles:%s";

    /**
     * 角色菜单权限缓存 Key 模式：role:menus:{roleLabel}
     */
    private static final String ROLE_MENUS_KEY = "role:menus:%s";

    // ============================= 用户角色缓存 =============================

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserRoles(Long userId) {
        try {
            String key = String.format(USER_ROLES_KEY, userId);
            Object cached = redisCache.get(key);
            if (cached != null) {
                return (List<String>) cached;
            }

            // 缓存未命中，查询数据库
            List<String> roleLabels = roleMapper.selectRoleLabelsByUserId(userId);
            redisCache.set(key, roleLabels, USER_ROLES_TTL);
            log.debug("缓存用户角色：userId={}, roles={}, ttl={}s", userId, roleLabels, USER_ROLES_TTL);
            return roleLabels;
        } catch (Exception e) {
            log.error("获取用户角色失败：userId={}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 缓存用户角色
     *
     * @param userId     用户ID
     * @param roleLabels 角色标识列表
     */
    public void cacheUserRoles(Long userId, List<String> roleLabels) {
        String key = String.format(USER_ROLES_KEY, userId);
        redisCache.set(key, roleLabels, USER_ROLES_TTL);
        log.debug("缓存用户角色：userId={}, roles={}, ttl={}s", userId, roleLabels, USER_ROLES_TTL);
    }

    /**
     * 删除用户角色缓存
     *
     * @param userId 用户ID
     */
    public void evictUserRoles(Long userId) {
        String key = String.format(USER_ROLES_KEY, userId);
        redisCache.delete(key);
        log.debug("删除用户角色缓存：userId={}", userId);
    }

    // ============================= 角色菜单权限缓存 =============================

    /**
     * 根据角色标识列表获取菜单权限
     * <p>
     * 缓存策略：Cache-Aside 懒加载 + 降级回填
     * <ol>
     *   <li>先从 Redis 缓存读取</li>
     *   <li>缓存未命中时，从数据库查询并回填缓存</li>
     *   <li>返回所有角色的权限去重后的列表</li>
     * </ol>
     * </p>
     *
     * @param roleLabels 角色标识列表
     * @return 菜单权限列表
     */
    @SuppressWarnings("unchecked")
    public List<MenuPermissionVO> getMenuByRoleLabels(List<String> roleLabels) {
        if (roleLabels == null || roleLabels.isEmpty()) {
            return Collections.emptyList();
        }

        Set<MenuPermissionVO> allPermissions = new HashSet<>();

        for (String roleLabel : roleLabels) {
            String key = String.format(ROLE_MENUS_KEY, roleLabel);
            Object cached = redisCache.get(key);
            if (cached != null) {
                // 缓存命中，直接使用
                allPermissions.addAll((List<MenuPermissionVO>) cached);
            } else {
                // 缓存未命中，从数据库查询并回填缓存
                log.debug("角色权限缓存未命中，从数据库加载：roleLabel={}", roleLabel);
                List<MenuPermissionVO> menuPermissions = roleMapper.selectMenuPermissionsByRoleLabel(roleLabel);
                if (menuPermissions != null && !menuPermissions.isEmpty()) {
                    // 回填缓存
                    cacheRoleMenuPermissions(roleLabel, menuPermissions);
                    allPermissions.addAll(menuPermissions);
                }
            }
        }

        return new ArrayList<>(allPermissions);
    }

    /**
     * 缓存角色的菜单权限
     *
     * @param roleLabel         角色标识
     * @param menuPermissionsVO 菜单权限列表
     */
    public void cacheRoleMenuPermissions(String roleLabel, List<MenuPermissionVO> menuPermissionsVO) {
        String key = String.format(ROLE_MENUS_KEY, roleLabel);
        redisCache.set(key, menuPermissionsVO, ROLE_MENUS_TTL);
        log.debug("缓存角色菜单权限：roleLabel={}, permissions={}, ttl={}s", roleLabel, menuPermissionsVO.size(), ROLE_MENUS_TTL);
    }

    /**
     * 删除角色菜单权限缓存
     *
     * @param roleLabel 角色标识
     */
    public void evictRoleMenuPermissions(String roleLabel) {
        String key = String.format(ROLE_MENUS_KEY, roleLabel);
        redisCache.delete(key);
        log.debug("删除角色菜单权限缓存：roleLabel={}", roleLabel);
    }

    // ============================= 批量操作 =============================

    /**
     * 刷新路由权限缓存
     * <p>
     * 批量将路由权限规则写入 Redis Hash
     * </p>
     *
     * @param routePermMap 路由权限规则映射（路由键 -> 权限码）
     */
    public void refreshRoutePermissionCache(Map<String, String> routePermMap) {
        String cacheKey = "sys:auth:route_map";

        // 1. 删除旧缓存
        redisCache.delete(cacheKey);

        // 2. 批量写入新缓存
        if (!routePermMap.isEmpty()) {
            Map<String, Object> cacheMap = new HashMap<>(routePermMap);
            redisCache.hSetAll(cacheKey, cacheMap);
        }

        log.debug("路由权限缓存已刷新：{} 条规则", routePermMap.size());
    }

    /**
     * 获取路由权限缓存
     *
     * @return 路由权限规则映射
     */
    public Map<String, String> getRoutePermissionCache() {
        String cacheKey = "sys:auth:route_map";
        Map<Object, Object> rawMap = redisCache.hGetAll(cacheKey);
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            result.put(key, value);
        }

        return result;
    }

    /**
     * 清空所有权限相关缓存
     */
    public void clearAllPermissionCache() {
        try {
            Collection<String> userKeys = redisCache.keys("user:roles:*");
            if (userKeys != null && !userKeys.isEmpty()) {
                redisCache.delete(userKeys);
            }

            Collection<String> roleKeys = redisCache.keys("role:menus:*");
            if (roleKeys != null && !roleKeys.isEmpty()) {
                redisCache.delete(roleKeys);
            }

            log.info("已清空所有权限缓存");
        } catch (Exception e) {
            log.error("清空权限缓存失败", e);
        }
    }
}
