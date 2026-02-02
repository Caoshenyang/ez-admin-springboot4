# Cache Service 设计规范

## 概述

Cache Service 是负责缓存数据存取的专用服务层，遵循单一职责原则。本文档定义 Cache Service 的命名规范、职责边界和实现模式。

---

## 架构原则

### 核心原则

1. **单一职责**: Cache Service 只负责缓存的存取操作，不包含业务逻辑和数据库查询
2. **依赖倒置**: 业务层依赖缓存抽象，不直接依赖 Redis 客户端
3. **职责分离**: 业务层负责缓存策略（Cache-Aside），Cache Service 负责执行操作

### 分层职责

| 层级 | 职责 | 示例 |
|------|------|------|
| **Business Service** | 缓存策略、业务逻辑、数据库查询 | 判断何时加载缓存、何时失效 |
| **Cache Service** | 纯缓存 KV 操作 | `get`, `set`, `delete` |

---

## 命名规范

### 类命名

**模式**: `{Domain}CacheService`

| 示例 | 说明 |
|------|------|
| `UserCacheService` | 用户相关缓存 |
| `RoleCacheService` | 角色相关缓存 |
| `MenuCacheService` | 菜单相关缓存 |
| `DeptCacheService` | 部门相关缓存 |

**反例**:
- ❌ `AdminCache` - 缺少 Service 后缀，不明确
- ❌ `UserRedisService` - 不应暴露底层存储实现
- ❌ `CacheService` - 缺少领域信息

### 方法命名

#### 获取缓存

```java
// 模式: get + 缓存内容
List<RoleInfo> getUserRoles(Long userId);
MenuTreeVO getMenuTree(Long menuId);
```

#### 保存缓存

```java
// 模式: save + 缓存内容
void saveUserRoles(Long userId, List<RoleInfo> roles);
void saveMenuTree(Long menuId, MenuTreeVO menu);
```

#### 删除缓存

```java
// 模式: delete/evict + 缓存内容
void deleteUserRoles(Long userId);
void evictMenuCache(Long menuId);
```

#### 刷新缓存

```java
// 模式: refresh + 缓存内容
void refreshRoutePermissionCache(Map<String, String> routePermMap);
void refreshAllMenuCache();
```

---

## 代码结构

### 标准模板

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class XxxCacheService {

    private final RedisCache redisCache;

    // ============================= 缓存 TTL 配置 =============================

    /**
     * 缓存过期时间：30 天（秒）
     */
    private static final int CACHE_TTL = 30 * 24 * 60 * 60;

    // ============================= 缓存 Key 定义 =============================

    /**
     * 缓存 Key 模式：xxx:yyy:{id}
     */
    private static final String CACHE_KEY = "xxx:yyy:%s";

    // ============================= 缓存操作 =============================

    /**
     * 获取缓存
     * <p>
     * 只从缓存获取，不查询数据库
     * </p>
     *
     * @param id 参数说明
     * @return 缓存数据，不存在时返回 null
     */
    @SuppressWarnings("unchecked")
    public Xxx getXxx(Long id) {
        String key = String.format(CACHE_KEY, id);
        Object cached = redisCache.get(key);
        if (cached == null) {
            return null;
        }
        return (Xxx) cached;
    }

    /**
     * 保存缓存
     *
     * @param id  参数说明
     * @param xxx 缓存数据
     */
    public void saveXxx(Long id, Xxx xxx) {
        String key = String.format(CACHE_KEY, id);
        redisCache.set(key, xxx, CACHE_TTL);
        log.debug("缓存xxx：id={}, ttl={}s", id, CACHE_TTL);
    }

    /**
     * 删除缓存
     *
     * @param id 参数说明
     */
    public void deleteXxx(Long id) {
        String key = String.format(CACHE_KEY, id);
        redisCache.delete(key);
        log.debug("删除xxx缓存：id={}", id);
    }
}
```

---

## 职责边界

### Cache Service 应该做

✅ **纯缓存操作**
- Redis 的 `get`, `set`, `delete`, `hGet`, `hSet` 等操作
- 缓存 Key 的构造和管理
- 缓存 TTL 的配置
- 类型转换和反序列化

✅ **返回值规范**
- 缓存不存在时返回 `null`
- 不抛出业务异常

### Cache Service 不应该做

❌ **数据库查询**
```java
// 错误示例
public List<RoleInfo> getUserRoles(Long userId) {
    List<RoleInfo> roles = redisCache.get(key);
    if (roles == null) {
        roles = userRoleMapper.selectByUserId(userId);  // ❌ 不应在 Cache Service 查数据库
    }
    return roles;
}
```

❌ **复杂业务逻辑**
```java
// 错误示例
public List<RoleInfo> getUserRoles(Long userId) {
    List<RoleInfo> roles = redisCache.get(key);
    if (roles == null) {
        // ❌ 不应在 Cache Service 处理业务逻辑
        if (user.isAdmin()) {
            roles = getAllRoles();
        } else {
            roles = getNormalRoles();
        }
    }
    return roles;
}
```

❌ **跨域调用**
```java
// 错误示例
public void refreshUserCache(Long userId) {
    userCacheService.deleteUser(userId);  // ❌ 不应调用其他 Cache Service
    roleCacheService.deleteByUserId(userId);
}
```

---

## 缓存策略示例

### Cache-Aside 模式（业务层实现）

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCacheService userCacheService;
    private final SysUserRoleRelationService userRoleRelationService;

    /**
     * 获取当前用户角色
     * <p>
     * Cache-Aside 缓存策略：
     * 1. 先查缓存
     * 2. 缓存未命中，查数据库
     * 3. 将结果写入缓存
     * </p>
     */
    public List<RoleInfo> getCurrentUserRoles() {
        Long userId = StpUtil.getLoginIdAsLong();

        // 1. 先从缓存获取
        List<RoleInfo> cachedRoles = userCacheService.getUserRoles(userId);
        if (cachedRoles != null) {
            return cachedRoles;
        }

        // 2. 缓存未命中，从数据库查询
        List<RoleInfo> roleInfoList = userRoleRelationService.getRoleInfoByUserId(userId);

        // 3. 写入缓存
        if (!roleInfoList.isEmpty()) {
            userCacheService.saveUserRoles(userId, roleInfoList);
        }

        return roleInfoList;
    }
}
```

---

## 目录结构

```
src/main/java/com/ez/admin/service/cache/
├── UserCacheService.java        # 用户缓存
├── RoleCacheService.java        # 角色缓存
├── MenuCacheService.java        # 菜单缓存
└── DeptCacheService.java        # 部门缓存
```

---

## 命名检查清单

创建 Cache Service 时，确保符合以下规范：

- [ ] 类名遵循 `{Domain}CacheService` 模式
- [ ] 使用 `@Service` 注解
- [ ] 使用 `@RequiredArgsConstructor` 注入依赖
- [ ] 缓存 Key 定义为 `private static final` 常量
- [ ] TTL 定义为 `private static final` 常量
- [ ] 方法名清晰表达缓存操作（get/save/delete/refresh）
- [ ] JavaDoc 注释说明返回 `null` 的条件
- [ **关键** ] 不包含任何数据库查询逻辑
- [ **关键** ] 不包含复杂业务逻辑
- [ **关键** ] 不调用其他 Cache Service

---

## 附录: Redis Key 命名规范

### Key 模式

```
{业务域}:{数据类型}:{唯一标识}
```

### 示例

| 数据类型 | Key 模式 | 示例 |
|---------|---------|------|
| 用户角色 | `user:roles:{userId}` | `user:roles:1001` |
| 角色菜单 | `role:menus:{roleLabel}` | `role:menus:SUPER_ADMIN` |
| 路由权限 | `sys:auth:route_map` | `sys:auth:route_map` |
| 用户信息 | `user:info:{userId}` | `user:info:1001` |

### 注意事项

1. 使用 `:` 分隔层级
2. 使用小写字母和下划线
3. 包含明确的业务语义
4. 避免过长的 Key（建议 < 100 字符）

---

**最后更新**: 2026-02-02
**维护者**: ez-admin
