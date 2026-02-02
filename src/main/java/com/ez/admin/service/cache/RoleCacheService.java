package com.ez.admin.service.cache;

import com.ez.admin.common.infrastructure.redis.RedisCache;
import com.ez.admin.dto.system.menu.vo.MenuPermissionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 角色缓存服务
 * <p>
 * 只负责角色相关数据的 Redis 缓存存取操作，不包含业务逻辑和数据库查询。
 * 包括：角色菜单权限、角色数据权限等。
 * </p>
 * <p>
 * 缓存 TTL 策略：
 * <ul>
 *   <li>角色菜单权限：30 天（变更频率低）</li>
 * </ul>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleCacheService {

    private final RedisCache redisCache;

    // ============================= 缓存 TTL 配置 =============================

    /**
     * 角色菜单权限缓存过期时间：30 天（秒）
     */
    private static final int ROLE_MENUS_TTL = 30 * 24 * 60 * 60;

    // ============================= 缓存 Key 定义 =============================

    /**
     * 角色菜单权限缓存 Key 模式：role:menus:{roleLabel}
     */
    private static final String ROLE_MENUS_KEY = "role:menus:%s";

    // ============================= 角色菜单权限缓存 =============================

    /**
     * 获取角色的菜单权限
     * <p>
     * 只从缓存获取，不查询数据库
     * </p>
     *
     * @param roleLabel 角色标识
     * @return 菜单权限列表，缓存不存在时返回 null
     */
    @SuppressWarnings("unchecked")
    public List<MenuPermissionVO> getRoleMenuPermissions(String roleLabel) {
        String key = String.format(ROLE_MENUS_KEY, roleLabel);
        Object cached = redisCache.get(key);
        if (cached == null) {
            return null;
        }
        return (List<MenuPermissionVO>) cached;
    }

    /**
     * 缓存角色的菜单权限
     *
     * @param roleLabel         角色标识
     * @param menuPermissionsVO 菜单权限列表
     */
    public void saveRoleMenuPermissions(String roleLabel, List<MenuPermissionVO> menuPermissionsVO) {
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
            result.put((String) entry.getKey(), (String) entry.getValue());
        }

        return result;
    }

    /**
     * 清空所有权限相关缓存
     */
    public void clearAllPermissionCache() {
        try {
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
