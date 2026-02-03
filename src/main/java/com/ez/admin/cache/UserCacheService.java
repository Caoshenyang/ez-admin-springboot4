package com.ez.admin.cache;

import com.ez.admin.common.core.constant.RedisKeyConstants;
import com.ez.admin.common.infrastructure.redis.RedisCache;
import com.ez.admin.dto.system.vo.RoleInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户缓存服务
 * <p>
 * 只负责用户相关数据的 Redis 缓存存取操作，不包含业务逻辑和数据库查询。
 * 遵循单一职责原则，缓存策略由业务层决定。
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final RedisCache redisCache;

    /**
     * Redis 缓存过期时间（秒）
     * 与 Sa-Token 默认 session timeout 保持一致：30天
     */
    private static final long REDIS_CACHE_EXPIRE_SECONDS = 30 * 24 * 60 * 60L;

    /**
     * 获取用户的角色信息列表
     * <p>
     * 只从 Redis 缓存获取，不查询数据库。
     * 如果缓存不存在或类型不匹配，返回 null。
     * </p>
     *
     * @param userId 用户ID
     * @return 角色信息列表，缓存不存在时返回 null
     */
    @SuppressWarnings("unchecked")
    public List<RoleInfo> getUserRoles(Long userId) {
        String roleKey = RedisKeyConstants.USER_ROLES_KEY_PREFIX + userId;
        Object cachedRoles = redisCache.get(roleKey);

        if (cachedRoles == null) {
            return null;
        }
        return (List<RoleInfo>) cachedRoles;
    }

    /**
     * 缓存用户的角色信息列表
     *
     * @param userId       用户ID
     * @param roleInfoList 角色信息列表
     */
    public void saveUserRoles(Long userId, List<RoleInfo> roleInfoList) {
        if (roleInfoList == null || roleInfoList.isEmpty()) {
            return;
        }

        String roleKey = RedisKeyConstants.USER_ROLES_KEY_PREFIX + userId;
        redisCache.set(roleKey, roleInfoList, REDIS_CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);

        String roleLabels = roleInfoList.stream()
                .map(RoleInfo::getRoleLabel)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        log.debug("已缓存用户 {} 的角色信息：{}", userId, roleLabels);
    }

    /**
     * 删除用户的角色缓存
     * <p>
     * 用户登出或角色变更时调用
     * </p>
     *
     * @param userId 用户ID
     */
    public void deleteUserRoles(Long userId) {
        String roleKey = RedisKeyConstants.USER_ROLES_KEY_PREFIX + userId;
        redisCache.delete(roleKey);
        log.debug("已删除用户 {} 的角色缓存", userId);
    }
}
