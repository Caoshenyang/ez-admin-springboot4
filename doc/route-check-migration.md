# Sa-Token 路由拦截式鉴权改造说明

## 改造概述

本次改造将项目从**注解式鉴权**升级为**路由拦截式鉴权**，实现了更灵活、更易维护的权限管理方式。

---

## 改造内容

### 1. 数据库表结构变更

**文件**: `doc/ez-admin-postgres.sql`

在 `ez_admin_sys_menu` 表中新增两个字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `api_route` | VARCHAR(255) | 后端 API 路由地址（如 `/api/user`） |
| `api_method` | VARCHAR(20) | HTTP 方法（GET、POST、PUT、DELETE、PATCH） |

**数据库升级脚本**（针对已有数据库）：

```sql
-- 1. 添加新字段
ALTER TABLE ez_admin_sys_menu ADD COLUMN IF NOT EXISTS api_route VARCHAR(255);
COMMENT ON COLUMN ez_admin_sys_menu.api_route IS '后端API路由地址';

ALTER TABLE ez_admin_sys_menu ADD COLUMN IF NOT EXISTS api_method VARCHAR(20);
COMMENT ON COLUMN ez_admin_sys_menu.api_method IS 'HTTP方法【GET POST PUT DELETE PATCH】';

-- 2. 创建索引以提升查询性能
CREATE INDEX IF NOT EXISTS idx_menu_api_route ON ez_admin_sys_menu(api_route, api_method);

-- 3. 初始化路由权限数据（执行 doc/route-permission-init-data.sql）
\i doc/route-permission-init-data.sql
```

---

### 2. 实体类更新

**文件**: `src/main/java/com/ez/admin/modules/system/entity/SysMenu.java`

新增字段映射：

```java
@TableField("api_route")
@Schema(description = "后端API路由地址")
private String apiRoute;

@TableField("api_method")
@Schema(description = "HTTP方法【GET POST PUT DELETE PATCH】")
private String apiMethod;
```

---

### 3. Mapper 层新增方法

**文件**: `src/main/java/com/ez/admin/modules/system/mapper/SysMenuMapper.java`

新增路由权限查询方法：

```java
@Select("""
    SELECT menu_perm
    FROM ez_admin_sys_menu
    WHERE api_route = #{apiRoute}
      AND api_method = #{apiMethod}
      AND status = 1
      AND is_deleted = 0
    LIMIT 1
    """)
String selectPermByRoute(@Param("apiRoute") String apiRoute, @Param("apiMethod") String apiMethod);
```

---

### 4. Service 层新增方法

**文件**: `src/main/java/com/ez/admin/service/menu/MenuService.java`

```java
/**
 * 根据API路由和HTTP方法查询权限码
 */
public String getPermByRoute(String apiRoute, String apiMethod) {
    return menuMapper.selectPermByRoute(apiRoute, apiMethod);
}
```

---

### 5. Sa-Token 配置改造

**文件**: `src/main/java/com/ez/admin/common/web/SaTokenConfig.java`

**改造前**：只做登录校验，权限校验依赖注解

**改造后**：实现路由拦截式鉴权

```java
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final MenuService menuService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/api/**")
                .notMatch("/api/auth/login", "/api/auth/logout", "/api/install/**")
                .check(r -> {
                    // 1. 登录校验
                    StpUtil.checkLogin();

                    // 2. 路由拦截式权限校验
                    HttpServletRequest request = r.getRequest();
                    String requestUri = request.getRequestURI();
                    String method = request.getMethod();

                    // 从数据库查询该路由需要的权限码
                    String requiredPerm = menuService.getPermByRoute(requestUri, method);

                    // 如果找到了权限码，则进行权限校验
                    if (requiredPerm != null && !requiredPerm.isEmpty()) {
                        StpUtil.checkPermission(requiredPerm);
                    }
                    // 如果没有找到权限码，说明该路由不需要权限控制，直接放行
                });
        }));
    }
}
```

---

### 6. Controller 层改造

**删除所有 `@SaCheckPermission` 注解**

共改造了 6 个 Controller：
- `UserController.java`
- `RoleController.java`
- `MenuController.java`
- `DeptController.java`
- `DictController.java`
- `OperationLogController.java**

**改造前**：
```java
@PostMapping
@SaCheckPermission("system:user:create")
public R<Void> create(@RequestBody UserCreateReq request) {
    // ...
}
```

**改造后**：
```java
@PostMapping
public R<Void> create(@RequestBody UserCreateReq request) {
    // ...
}
```

所有 Controller 顶部添加了说明注释：
```java
/**
 * 用户管理控制器
 * <p>
 * 权限说明：本控制器的权限通过路由拦截式鉴权实现，无需使用 @SaCheckPermission 注解
 * </p>
 */
```

---

### 7. 初始数据准备

**文件**: `doc/route-permission-init-data.sql`

包含所有接口的路由权限映射数据，格式示例：

| menu_perm | api_route | api_method |
|-----------|-----------|------------|
| system:user:create | /api/user | POST |
| system:user:update | /api/user | PUT |
| system:user:delete | /api/user | DELETE |
| system:user:query | /api/user | GET |

---

## 使用方式

### 1. 新增接口时

**步骤**：

1. 在代码中正常编写 Controller 接口
2. 在数据库的 `ez_admin_sys_menu` 表中添加对应的菜单记录
3. 设置 `menu_perm`（权限码）、`api_route`（API路由）、`api_method`（HTTP方法）
4. 为角色分配该菜单权限

**示例**：

```java
// 1. 编写接口
@PostMapping("/export")
public R<Void> exportUsers() {
    // 导出用户逻辑
}

// 2. 在数据库中添加记录
INSERT INTO ez_admin_sys_menu (
    menu_id, menu_name, menu_perm, api_route, api_method,
    menu_type, status, parent_id, menu_sort
) VALUES (
    100, '导出用户', 'system:user:export', '/api/user/export', 'POST',
    3, 1, 1, 100
);
```

---

### 2. 修改接口路由时

如果修改了接口的 URL 路径或 HTTP 方法，需要同步更新数据库：

```sql
UPDATE ez_admin_sys_menu
SET api_route = '/api/user/new-path',
    api_method = 'PATCH'
WHERE menu_perm = 'system:user:update';
```

---

### 3. 查询路由权限配置

```sql
-- 查询所有已配置 API 路由的菜单
SELECT menu_id, menu_name, menu_perm, api_route, api_method
FROM ez_admin_sys_menu
WHERE api_route IS NOT NULL
ORDER BY menu_id;
```

---

## 优势对比

| 对比项 | 注解式鉴权（改造前） | 路由拦截式鉴权（改造后） |
|--------|---------------------|----------------------|
| **权限配置位置** | 代码注解 | 数据库 |
| **灵活性** | 低（需重新编译部署） | 高（动态配置，立即生效） |
| **可视化管理** | 无（需查看代码） | 有（可开发管理界面） |
| **维护成本** | 中（每个接口都要加注解） | 低（统一管理） |
| **代码整洁度** | 注解遍布代码 | 代码简洁，无注解 |
| **适用场景** | 权限固定的小项目 | 权限灵活的中大型项目 |

---

## 注意事项

### 1. 路由匹配规则

- 精确匹配：`/api/user` 只匹配 `/api/user`
- 不支持通配符：如需 `/api/user/**`，需要配置多条记录

### 2. 权限码规范

保持现有的权限码格式：`模块:功能:操作`
- `system:user:create` - 系统管理-用户管理-创建
- `system:user:update` - 系统管理-用户管理-更新
- `system:user:delete` - 系统管理-用户管理-删除
- `system:user:query` - 系统管理-用户管理-查询

### 3. 无需权限的接口

如果某个接口不需要权限控制，只需：
- 不在数据库中配置该路由的权限映射
- 系统会自动放行（前提是已登录）

### 4. 开发环境调试

在开发环境中，可以通过日志查看权限校验过程：

```yaml
logging:
  level:
    com.ez.admin.common.web: DEBUG
```

日志输出示例：
```
路由权限校验：POST /api/user 需要权限 system:user:create
路由无需权限控制：GET /api/public/info
```

---

## 升级步骤

### 1. 备份数据库

```bash
pg_dump -U your_username -d your_database > backup.sql
```

### 2. 执行数据库升级

```bash
psql -U your_username -d your_database -f doc/ez-admin-postgres.sql
```

### 3. 初始化路由权限数据

```bash
psql -U your_username -d your_database -f doc/route-permission-init-data.sql
```

### 4. 重新编译项目

```bash
mvn clean package -DskipTests
```

### 5. 启动项目并测试

```bash
mvn spring-boot:run
```

---

## 常见问题

### Q1: 如何查看某个路由需要的权限？

```sql
SELECT menu_perm, api_route, api_method
FROM ez_admin_sys_menu
WHERE api_route = '/api/user' AND api_method = 'POST';
```

### Q2: 如何临时禁用某个接口的权限校验？

```sql
-- 方式1：清空权限码
UPDATE ez_admin_sys_menu
SET menu_perm = ''
WHERE api_route = '/api/user' AND api_method = 'POST';

-- 方式2：禁用菜单
UPDATE ez_admin_sys_menu
SET status = 0
WHERE api_route = '/api/user' AND api_method = 'POST';
```

### Q3: 如何批量更新路由前缀？

如果 API 路由前缀从 `/api` 改为 `/api/v1`：

```sql
UPDATE ez_admin_sys_menu
SET api_route = regexp_replace(api_route, '^/api', '/api/v1')
WHERE api_route LIKE '/api/%';
```

---

## 后续优化建议

1. **开发管理界面**：在菜单管理中添加 API 路由配置功能
2. **路由自动发现**：启动时扫描所有 Controller 接口，自动生成路由权限映射
3. **权限可视化**：在 Swagger 文档中显示接口需要的权限
4. **批量导入导出**：支持路由权限配置的批量导入导出

---

## 改造完成

改造已完成！项目现在使用 Sa-Token 路由拦截式鉴权，更加灵活和易维护。
