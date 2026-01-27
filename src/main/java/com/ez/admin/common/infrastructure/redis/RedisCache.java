package com.ez.admin.common.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具类
 * <p>
 * 提供 Redis 常用操作的封装方法
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-19
 */
@Component
@RequiredArgsConstructor
public class RedisCache {

    private final RedisTemplate<String, Object> redisTemplate;

    // =============================  基本操作  =============================

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置缓存（带过期时间）
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置缓存（带过期时间，秒）
     *
     * @param key     键
     * @param value   值
     * @param seconds 过期时间（秒）
     */
    public void set(String key, Object value, long seconds) {
        set(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return true: 删除成功，false: 删除失败
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除缓存
     *
     * @param keys 键集合
     * @return 删除数量
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 键
     * @return true: 存在，false: 不存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     *
     * @param key     键
     * @param timeout 过期时间
     * @param unit    时间单位
     * @return true: 设置成功，false: 设置失败
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间（秒）
     *
     * @param key     键
     * @param seconds 过期时间（秒）
     * @return true: 设置成功，false: 设置失败
     */
    public Boolean expire(String key, long seconds) {
        return expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return 过期时间（秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // =============================  Hash 操作  =============================

    /**
     * 设置 Hash 缓存
     *
     * @param key     键
     * @param hashKey Hash 键
     * @param value   值
     */
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * 批量设置 Hash 缓存
     * <p>
     * 一次性设置多个 Hash 键值对，减少网络 IO 次数
     * </p>
     *
     * @param key 键
     * @param map Hash 键值对
     */
    public void hSetAll(String key, Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            redisTemplate.opsForHash().putAll(key, map);
        }
    }

    /**
     * 获取 Hash 缓存
     *
     * @param key     键
     * @param hashKey Hash 键
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 获取所有 Hash 值
     *
     * @param key 键
     * @return Hash Map
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除 Hash 缓存
     *
     * @param key      键
     * @param hashKeys Hash 键集合
     * @return 删除数量
     */
    public Long hDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    /**
     * 判断 Hash 字段是否存在
     *
     * @param key     键
     * @param hashKey Hash 键
     * @return true: 存在，false: 不存在
     */
    public Boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    // =============================  其他操作  =============================

    /**
     * 获取匹配模式的所有键
     *
     * @param pattern 匹配模式（如：user:*）
     * @return 键集合
     */
    public Collection<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
}
