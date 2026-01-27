# 超级管理员权限自动同步设计文档

## 概述

为了确保 SUPER_ADMIN 角色始终拥有所有菜单权限，系统实现了权限自动同步机制，无需手动分配权限。

## 设计原则

### ✅ 统一分配（推荐）
- 所有权限记录在数据库，可审计
- 权限校验逻辑统一，无需特殊判断
- 符合安全最佳实践

### ❌ 后门方式（不推荐）
- 权限不透明，无法审计
- 逻辑分散，难以维护
- 存在安全隐患

---

## 核心组件

### 1. SuperAdminPermissionService
**路径**: `com.ez.admin.common.component.SuperAdminPermissionService`

**核心方法**:
```java
/**
 * 同步超级管理员权限
 * - 查询所有启用状态的菜单
 * - 删除 SUPER_ADMIN 原有的菜单关联
 * - 重新建立所有菜单关联
 * - 刷新缓存
 */
public int syncSuperAdminPermissions()
```

**特性**:
- 事务保证数据一致性
- 自动处理缓存刷新
- 详细的日志记录

### 2. MenuService 触发同步
**路径**: `com.ez.admin.service.menu.MenuService`

**触发时机**:
- ✅ 创建菜单后 (`createMenu`)
- ✅ 更新菜单后 (`updateMenu`)
- ✅ 删除菜单后 (`deleteMenu`)

**示例**:
```java
// MenuService.java
public void createMenu(MenuCreateReq request) {
    // ... 创建菜单逻辑

    // 自动同步超级管理员权限
    superAdminPermissionService.syncSuperAdminPermissions();
}
```

### 3. 管理接口（手动触发）
**路径**: `com.ez.admin.api.system.SystemController`

**接口**:
```
POST /api/system/sync-super-admin-permissions
```

**响应示例**:
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "menuCount": 25,
    "roleLabel": "SUPER_ADMIN",
    "message": "超级管理员权限同步成功"
  }
}
```

**适用场景**:
- 批量导入菜单后
- 手动修复权限数据
- 系统升级后

### 4. 启动监听器（可选）
**路径**: `com.ez.admin.common.listener.SuperAdminPermissionStartupListener`

**功能**:
- 应用启动完成后自动执行一次权限同步
- 确保 SUPER_ADMIN 权限与数据库一致

**配置控制**:
```yaml
ez:
  admin:
    super-admin:
      auto-sync: true  # 默认 true，设为 false 可禁用
```

---

## 使用指南

### 场景 1: 正常开发
**操作**: 无需任何操作
**说明**:
- 新增/修改/删除菜单时自动触发同步
- SUPER_ADMIN 自动获得最新权限
- 无需手动维护

### 场景 2: 批量导入菜单
**操作**:
```bash
# 1. 执行菜单批量导入 SQL
# 2. 调用同步接口
curl -X POST http://localhost:8080/api/system/sync-super-admin-permissions \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 场景 3: 系统升级
**操作**:
1. 重启应用（自动触发同步）
2. 或手动调用同步接口

### 场景 4: 数据修复
**操作**: 手动调用同步接口
```bash
curl -X POST http://localhost:8080/api/system/sync-super-admin-permissions
```

---

## 权限校验逻辑

### 统一的校验流程
```java
// SaTokenPermissionImpl.java
@Override
public boolean hasPermission(String permission) {
    // 1. 获取用户角色
    List<String> roles = getUserRoles();

    // 2. 查询角色权限（从缓存/数据库）
    List<String> permissions = getPermissionsByRoles(roles);

    // 3. 校验权限
    return permissions.contains(permission);
}
```

**关键点**:
- ✅ SUPER_ADMIN 也需要通过正常的权限校验流程
- ✅ 权限来源于数据库中的菜单关联
- ✅ 无需在代码中写 `if (superAdmin) return true`

---

## 配置选项

### application.yml
```yaml
ez:
  admin:
    super-admin:
      # 是否在启动时自动同步权限
      auto-sync: true  # 默认 true
```

---

## 数据库设计

### 角色表 (ez_admin_sys_role)
```sql
INSERT INTO ez_admin_sys_role (
    role_name, role_label, role_sort, data_scope, status, description
) VALUES (
    '超级管理员',
    'SUPER_ADMIN',  -- 角色标识
    1,
    5,  -- 全部数据权限
    1,  -- 启用
    '系统超级管理员角色，拥有所有权限'
);
```

### 角色菜单关联表 (ez_admin_sys_role_menu_relation)
```sql
-- 自动同步时会自动维护此表
-- 示例：SUPER_ADMIN 拥有所有菜单
INSERT INTO ez_admin_sys_role_menu_relation (role_id, menu_id)
SELECT
    (SELECT role_id FROM ez_admin_sys_role WHERE role_label = 'SUPER_ADMIN'),
    menu_id
FROM ez_admin_sys_menu
WHERE is_deleted = 0 AND status = 1;
```

---

## 监控与日志

### 日志级别
```yaml
logging:
  level:
    com.ez.admin.common.component.SuperAdminPermissionService: INFO
    com.ez.admin.service.menu.MenuService: INFO
```

### 关键日志
```log
INFO  - 开始同步超级管理员权限...
INFO  - 超级管理员权限同步成功，角色ID：1，菜单数量：25
INFO  - 角色菜单权限缓存已刷新：roleLabel=SUPER_ADMIN
```

---

## 注意事项

### ✅ DO（推荐做法）
1. 依赖自动同步机制维护 SUPER_ADMIN 权限
2. 批量导入菜单后手动调用同步接口
3. 监控日志确保同步成功

### ❌ DON'T（禁止做法）
1. ❌ 在权限校验代码中写 `if (superAdmin) return true`
2. ❌ 手动维护 SUPER_ADMIN 的菜单权限关系
3. ❌ 禁用启动监听器后忘记手动同步

---

## 测试验证

### 1. 验证自动同步
```bash
# 1. 新增菜单
POST /api/menu/create

# 2. 查询 SUPER_ADMIN 角色权限
GET /api/role/1

# 3. 验证新菜单已在权限列表中
```

### 2. 验证启动同步
```bash
# 1. 重启应用
mvn spring-boot:run

# 2. 查看日志
grep "超级管理员权限自动同步完成" logs/spring.log
```

### 3. 验证手动同步
```bash
# 调用同步接口
curl -X POST http://localhost:8080/api/system/sync-super-admin-permissions

# 检查返回结果
{
  "data": {
    "menuCount": 25,
    "message": "超级管理员权限同步成功"
  }
}
```

---

## 常见问题

### Q: 为什么不在权限校验时直接判断 SUPER_ADMIN？
**A**:
- 降低可审计性
- 增加安全风险
- 维护困难
- 不符合最佳实践

### Q: 自动同步会影响性能吗？
**A**:
- 仅在菜单变更时触发，频率很低
- 使用批量插入，性能开销可忽略
- 事务保证一致性，不会导致脏数据

### Q: 如果同步失败会怎样？
**A**:
- 事务回滚，不影响原有数据
- 错误日志记录，便于排查
- 可通过手动接口重试

---

## 总结

本设计实现了：
1. ✅ 权限透明可审计
2. ✅ 逻辑统一易维护
3. ✅ 自动同步免维护
4. ✅ 安全可靠有保障
