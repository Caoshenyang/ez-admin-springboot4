# 数据权限实现说明

## 一、功能概述

数据权限功能已完成实现，基于 MyBatis-Plus 的 `DataPermissionInterceptor` 插件，根据用户的角色和数据权限范围，自动在 SQL 查询中添加数据过滤条件。

### 核心特性

✅ **自动过滤**：查询业务表时自动添加数据权限条件，无需手动编码
✅ **灵活配置**：支持 5 级数据权限范围
✅ **性能优化**：使用 Sa-Token Session 缓存数据权限信息，避免重复查询数据库
✅ **易于扩展**：只需将表名添加到白名单即可启用数据权限

---

## 二、数据权限范围说明

| 数据权限范围 | 值 | 说明 | SQL 条件示例 |
|------------|---|------|------------|
| **仅本人数据** | 1 | 只能查看自己创建的数据 | `created_by = 当前用户ID` |
| **本部门数据** | 2 | 可以查看本部门所有人员创建的数据 | `created_by IN (SELECT user_id FROM sys_user WHERE dept_id = 当前部门ID)` |
| **本部门及以下** | 3 | 可以查看本部门及子部门所有人员创建的数据 | `created_by IN (SELECT user_id FROM sys_user WHERE dept_id IN (子部门列表))` |
| **自定义数据** | 4 | 可以查看指定部门所有人员创建的数据 | `created_by IN (SELECT user_id FROM sys_user WHERE dept_id IN (自定义部门列表))` |
| **全部数据** | 5 | 可以查看所有数据 | 无过滤条件 |

---

## 三、已实现的文件清单

### 核心类

| 文件路径 | 说明 |
|---------|------|
| `common/framework/datascope/DataScopeInfo.java` | 数据权限信息类 |
| `common/framework/datascope/DataScopeContext.java` | 数据权限上下文管理类 |
| `common/framework/datascope/DataScopeService.java` | 数据权限服务（计算用户的数据权限范围） |
| `common/framework/datascope/UserDataPermissionHandler.java` | 数据权限处理器（生成 SQL 过滤条件） |
| `common/framework/datascope/DataScopeInterceptor.java` | 数据权限拦截器（从 Session 恢复上下文） |

### 配置类

| 文件路径 | 说明 |
|---------|------|
| `common/infrastructure/mybatis/MybatisPlusConfig.java` | MyBatis-Plus 配置（已注册数据权限拦截器） |
| `common/infrastructure/web/config/SaTokenMvcConfig.java` | MVC 配置（已注册数据权限拦截器） |

### 业务类（测试用）

| 文件路径 | 说明 |
|---------|------|
| `modules/system/entity/TestOrder.java` | 测试订单实体 |
| `modules/system/mapper/TestOrderMapper.java` | 测试订单 Mapper |
| `service/order/OrderService.java` | 测试订单服务 |
| `api/order/OrderController.java` | 测试订单控制器 |
| `dto/order/vo/OrderListVO.java` | 订单列表 VO |

### 数据库脚本

| 文件路径 | 说明 |
|---------|------|
| `doc/sql/test-order-table.sql` | 测试订单表建表脚本 |

---

## 四、如何测试数据权限功能

### 步骤 1：执行数据库脚本

```bash
psql -U your_username -d your_database -f doc/sql/test-order-table.sql
```

### 步骤 2：启动项目

```bash
mvn spring-boot:run
```

### 步骤 3：登录系统

```bash
# 使用超级管理员登录（默认全部数据权限）
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123456"
  }'

# 保存返回的 token
```

### 步骤 4：测试订单查询接口

```bash
# 查询订单列表（会自动应用数据权限过滤）
curl -X GET "http://localhost:8080/api/order/list" \
  -H "Authorization: Bearer {token}"

# 分页查询订单列表
curl -X GET "http://localhost:8080/api/order/page?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer {token}"
```

### 步骤 5：验证数据权限

**测试场景 1：仅本人数据权限**

1. 创建一个测试用户，分配"仅本人数据"的角色
2. 使用该用户登录
3. 查询订单列表，应该只能看到自己创建的订单

**测试场景 2：本部门数据权限**

1. 创建一个测试用户，分配"本部门数据"的角色
2. 使用该用户登录
3. 查询订单列表，应该能看到本部门所有用户创建的订单

**测试场景 3：全部数据权限**

1. 使用超级管理员登录
2. 查询订单列表，应该能看到所有订单

---

## 五、如何给新表添加数据权限

### 步骤 1：确保表有 `created_by` 字段

```sql
CREATE TABLE ez_admin_your_table (
    -- ... 其他字段
    created_by BIGINT NOT NULL,
    -- ... 其他字段
);
```

### 步骤 2：将表名添加到白名单

修改 `UserDataPermissionHandler.java`：

```java
private static final List<String> DATA_PERMISSION_TABLES = List.of(
    "ez_admin_test_order",
    "ez_admin_your_table",  // 新增的表
    // 后续添加新表时，在此处注册表名
);
```

### 步骤 3：正常编写查询代码

```java
// Service 中正常查询，数据权限会自动生效
List<YourEntity> list = yourMapper.selectList(null);
```

---

## 六、工作原理

### 1. 登录流程

```
用户登录
  ↓
验证用户名和密码
  ↓
查询用户的角色和数据权限范围（DataScopeService）
  ↓
将数据权限信息存入 Sa-Token Session
  ↓
返回 token 给前端
```

### 2. 请求流程

```
前端发送请求（携带 token）
  ↓
Sa-Token 拦截器校验登录和权限
  ↓
数据权限拦截器从 Session 恢复数据权限信息到 ThreadLocal
  ↓
执行数据库查询
  ↓
数据权限拦截器拦截 SQL，根据数据权限范围自动添加过滤条件
  ↓
返回过滤后的数据
  ↓
请求结束后清理 ThreadLocal
```

### 3. SQL 过滤示例

**原始 SQL**：
```sql
SELECT * FROM ez_admin_test_order WHERE is_deleted = 0
```

**数据权限 = 仅本人（userId = 1）**：
```sql
SELECT * FROM ez_admin_test_order
WHERE is_deleted = 0
  AND created_by = 1
```

**数据权限 = 本部门（deptId = 2）**：
```sql
SELECT * FROM ez_admin_test_order
WHERE is_deleted = 0
  AND created_by IN (
    SELECT user_id FROM ez_admin_sys_user
    WHERE dept_id = 2
  )
```

---

## 七、注意事项

1. **表必须有 `created_by` 字段**：数据权限功能依赖此字段来过滤数据
2. **白名单机制**：只有在白名单中的表才会进行数据权限过滤
3. **超级管理员**：拥有"超级管理员"角色的用户自动拥有全部数据权限
4. **性能考虑**：数据权限会自动生成子查询，对于大数据量场景建议优化索引
5. **调试日志**：可以开启日志查看数据权限 SQL 片段生成过程：
   ```yaml
   logging:
     level:
       com.ez.admin.common.framework.datascope: DEBUG
   ```

---

## 八、常见问题

### Q1：为什么查询不到任何数据？

**A**：可能原因：
1. 数据权限信息未正确设置，检查登录日志
2. 表未添加到白名单
3. 当前用户的数据权限范围内确实没有数据

### Q2：如何查看当前用户的 SQL 过滤条件？

**A**：开启 SQL 日志：
```yaml
logging:
  level:
    com.ez.admin.modules.system.mapper: DEBUG
```

### Q3：数据权限会影响性能吗？

**A**：
- 数据权限会生成子查询，会有一定的性能开销
- 建议在 `created_by` 和 `dept_id` 字段上建立索引
- 对于查询频繁的表，可以考虑缓存

### Q4：如何临时禁用某个表的数据权限？

**A**：从白名单中移除该表名即可：
```java
private static final List<String> DATA_PERMISSION_TABLES = List.of(
    // "ez_admin_test_order"  // 注释掉即可禁用
);
```

---

## 九、后续优化建议

1. **缓存优化**：将数据权限信息缓存到 Redis，减少 Sa-Token Session 的压力
2. **性能监控**：添加数据权限 SQL 的执行时间监控
3. **动态白名单**：支持通过配置文件或数据库动态配置白名单
4. **字段自定义**：支持自定义过滤字段名（不限于 `created_by`）
5. **多字段支持**：支持同时过滤多个字段（如 `created_by` 和 `dept_id`）

---

**文档更新日期**：2026-01-28
**作者**：ez-admin
