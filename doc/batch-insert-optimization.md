# 批量插入优化记录

## 问题描述

在代码审查中发现 **4 处**严重的性能反模式：在循环中执行数据库插入操作，导致 N+1 查询问题。

### 反模式示例

```java
// ❌ 反模式：循环插入（N 次网络 IO）
allMenus.forEach(menu -> {
    SysRoleMenuRelation relation = new SysRoleMenuRelation();
    relation.setRoleId(roleId);
    relation.setMenuId(menu.getMenuId());
    roleMenuRelationMapper.insert(relation);  // 每次循环都执行一次 SQL INSERT
});
```

**影响**：
- 假设有 100 条菜单 = 100 次 SQL INSERT = 100 次网络 IO
- 数据库连接池压力增大
- 响应时间线性增长

---

## 优化方案

### 修复前（❌ 反模式）

```java
relations.forEach(roleMenuRelationMapper::insert);
```

### 修复后（✅ 批量插入）

```java
// 1. 构建 Entity 列表
List<SysRoleMenuRelation> relations = menuIds.stream()
    .map(menuId -> {
        SysRoleMenuRelation relation = new SysRoleMenuRelation();
        relation.setRoleId(roleId);
        relation.setMenuId(menuId);
        return relation;
    })
    .collect(Collectors.toList());

// 2. 使用 MyBatis-Plus IService.saveBatch 批量插入
if (!relations.isEmpty()) {
    roleMenuRelationService.saveBatch(relations);
}
```

---

## 性能对比

| 场景 | 循环插入（旧） | 批量插入（新） | 性能提升 |
|------|--------------|--------------|---------|
| **10 条记录** | 10 次 SQL | 1 次 Batch | ~10x |
| **100 条记录** | 100 次 SQL | 1 次 Batch | ~100x |
| **网络 IO** | N 次 | 1 次 | N 倍 |
| **事务开销** | N 次 | 1 次 | N 倍 |

**MyBatis-Plus `saveBatch` 底层优化**：
- 使用 `ExecutorType.BATCH` 模式
- 使用 JDBC `PreparedStatement.addBatch()`
- 缓存 SQL 语句，批量提交
- 减少网络往返次数

---

## 修复清单

| 文件 | 行数 | 修复内容 |
|------|------|---------|
| `PermissionService.java` | 82-98 | 超级管理员权限同步批量插入 |
| `RoleService.java` | 285 | 角色分配菜单批量插入 |
| `RoleService.java` | 337 | 角色分配部门批量插入 |
| `UserService.java` | 260 | 用户分配角色批量插入 |

---

## 技术规范

### ✅ 推荐做法

```java
// 1. 构建 Entity 列表
List<Entity> entities = ...

// 2. 注入对应的 IService
private final XxxEntityService xxxEntityService;

// 3. 批量插入
if (!entities.isEmpty()) {
    xxxEntityService.saveBatch(entities);
}
```

### ❌ 禁止做法

```java
// 禁止：在 forEach/for 循环中调用 Mapper.insert/update/delete
entities.forEach(entity -> mapper.insert(entity));     // ❌
entities.forEach(mapper::insert);                       // ❌
for (Entity e : entities) { mapper.insert(e); }        // ❌
```

---

## 其他注意事项

### 1. 批量大小控制

MyBatis-Plus `saveBatch` 默认批量大小为 **1000**，可通过第二个参数调整：

```java
// 每批 500 条
xxxService.saveBatch(entities, 500);
```

### 2. 事务管理

批量插入应放在 `@Transactional` 中，确保原子性：

```java
@Transactional(rollbackFor = Exception.class)
public void assignMenus(Long roleId, List<Long> menuIds) {
    // 1. 删除旧关联
    roleMenuRelationMapper.delete(...);

    // 2. 批量插入新关联
    roleMenuRelationService.saveBatch(relations);
}
```

### 3. 性能监控

建议对批量操作添加性能日志：

```java
long startTime = System.currentTimeMillis();
roleMenuRelationService.saveBatch(relations);
log.info("批量插入完成，数量：{}，耗时：{}ms", relations.size(), System.currentTimeMillis() - startTime);
```

---

## 总结

**核心原则**：
- **严禁在循环中执行数据库插入、更新、删除操作**
- **使用 MyBatis-Plus `IService.saveBatch/saveOrUpdateBatch` 进行批量操作**
- **批量操作能带来 10-100 倍的性能提升**

**参考**：
- MyBatis-Plus 官方文档：https://baomidou.com/pages/49cc81/
- JDBC Batch 优化原理：https://docs.oracle.com/javase/tutorial/jdbc/basics/batch.html
