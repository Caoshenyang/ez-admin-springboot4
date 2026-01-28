# 操作日志 AOP 切面功能说明

## 一、功能概述

操作日志功能已完善实现，通过 AOP 切面自动记录用户的关键操作行为。

### 核心特性

✅ **自动记录**：通过 `@OperationLog` 注解自动拦截方法并记录日志
✅ **异步保存**：使用 `@Async` 异步保存日志，不影响业务性能
✅ **信息完整**：记录模块、操作、用户信息、请求信息、执行结果等
✅ **异常捕获**：自动记录操作失败的错误信息
✅ **IP 获取**：支持多级代理，准确获取客户端真实 IP

---

## 二、已优化的内容

### 1. 切面优化（OperationLogAspect）

**优化点**：

| 优化项 | 优化前 | 优化后 |
|--------|--------|--------|
| **用户名获取** | 使用 `loginId.toString()` | 从 Sa-Token Session 获取真实用户名 |
| **日志保存** | 同步保存 | 异步保存（`@Async`） |
| **参数过滤** | 不过滤 | 过滤 HttpServletRequest/Response/MultipartFile |
| **错误信息** | 不限制长度 | 限制 1000 字符 |
| **参数长度** | 限制 2000 字符 | 保持 2000 字符限制 |

### 2. 异步支持

在 `EzAdminApplication` 主类上添加了 `@EnableAsync` 注解：

```java
@EnableAsync
@SpringBootApplication(scanBasePackages = "com.ez.admin")
public class EzAdminApplication {
    // ...
}
```

### 3. 关键接口注解

已为以下 Controller 的关键接口添加 `@OperationLog` 注解：

| Controller | 已添加注解的接口 |
|-----------|----------------|
| **UserController** | 创建、更新、删除、批量删除、上传头像、修改个人信息、修改密码、切换状态 |
| **DeptController** | 创建、更新、删除 |
| **RoleController** | 创建、更新、删除、分配菜单、分配部门 |
| **ConfigController** | 创建、更新、删除、刷新缓存 |
| **SystemController** | 部门统计、用户统计 |

---

## 三、使用方法

### 3.1 在 Controller 方法上添加注解

```java
@PostMapping
@OperationLog(module = "用户管理", operation = "创建", description = "创建用户")
public R<Void> create(@Valid @RequestBody UserCreateReq request) {
    userService.createUser(request);
    return R.success("创建成功");
}
```

**注解参数说明**：

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| `module` | String | 是 | 模块名称 | "用户管理"、"角色管理" |
| `operation` | String | 是 | 操作类型 | "创建"、"更新"、"删除" |
| `description` | String | 是 | 操作描述 | "创建用户"、"删除角色" |

### 3.2 推荐的注解使用规范

**增删改操作**：必须添加

```java
@PostMapping
@OperationLog(module = "用户管理", operation = "创建", description = "创建用户")
public R<Void> create(@RequestBody UserCreateReq request) { }

@PutMapping
@OperationLog(module = "用户管理", operation = "更新", description = "更新用户信息")
public R<Void> update(@RequestBody UserUpdateReq request) { }

@DeleteMapping("/{userId}")
@OperationLog(module = "用户管理", operation = "删除", description = "删除用户")
public R<Void> delete(@PathVariable Long userId) { }
```

**查询操作**：不需要添加（避免日志过多）

```java
@GetMapping("/{userId}")
public R<UserDetailVO> getById(@PathVariable Long userId) { }

@PostMapping("/page")
public R<PageVO<UserListVO>> getPage(@RequestBody PageQuery query) { }
```

**重要业务操作**：必须添加

```java
@PostMapping("/assign/roles")
@OperationLog(module = "用户管理", operation = "分配角色", description = "为用户分配角色")
public R<Void> assignRoles(@RequestBody UserAssignRoleReq request) { }

@PutMapping("/password")
@OperationLog(module = "用户管理", operation = "修改密码", description = "修改当前用户密码")
public R<Void> changePassword(@RequestBody UserPasswordChangeReq request) { }
```

---

## 四、记录的日志内容

### 4.1 日志字段

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| `log_id` | Long | 日志ID | 1234567890 |
| `module` | String | 模块名称 | "用户管理" |
| `operation` | String | 操作类型 | "创建" |
| `description` | String | 操作描述 | "创建用户" |
| `user_id` | Long | 操作用户ID | 1 |
| `username` | String | 操作用户名 | "admin" |
| `request_method` | String | 请求方法 | "POST" |
| `request_url` | String | 请求URL | "/api/user" |
| `request_ip` | String | 请求IP | "192.168.1.100" |
| `request_params` | String | 请求参数（JSON） | `{"username":"test","nickname":"测试用户"}` |
| `execute_time` | Long | 执行时长（毫秒） | 125 |
| `status` | Integer | 操作状态（0 失败 1 成功） | 1 |
| `error_msg` | String | 错误信息（失败时） | "用户名已存在" |
| `create_time` | LocalDateTime | 创建时间 | 2026-01-28 10:30:00 |

### 4.2 日志示例

**成功操作**：
```json
{
  "logId": 1234567890,
  "module": "用户管理",
  "operation": "创建",
  "description": "创建用户",
  "userId": 1,
  "username": "admin",
  "requestMethod": "POST",
  "requestUrl": "/api/user",
  "requestIp": "192.168.1.100",
  "requestParams": "{\"username\":\"test\",\"nickname\":\"测试用户\",\"deptId\":2,\"roleIds\":[1,2]}",
  "executeTime": 125,
  "status": 1,
  "errorMsg": null,
  "createTime": "2026-01-28T10:30:00"
}
```

**失败操作**：
```json
{
  "logId": 1234567891,
  "module": "用户管理",
  "operation": "创建",
  "description": "创建用户",
  "userId": 1,
  "username": "admin",
  "requestMethod": "POST",
  "requestUrl": "/api/user",
  "requestIp": "192.168.1.100",
  "requestParams": "{\"username\":\"admin\",\"nickname\":\"管理员\"}",
  "executeTime": 45,
  "status": 0,
  "errorMsg": "用户名已存在",
  "createTime": "2026-01-28T10:31:00"
}
```

---

## 五、查询操作日志

### 5.1 分页查询接口

```bash
POST /api/log/page
Content-Type: application/json

{
  "pageNum": 1,
  "pageSize": 10,
  "keyword": "admin",           # 可选：模糊搜索用户名、模块、描述
  "conditions": [               # 可选：高级查询条件
    {"field": "module", "operator": "EQ", "value": "用户管理"},
    {"field": "status", "operator": "EQ", "value": 1}
  ]
}
```

### 5.2 查询条件支持

| 字段 | 操作符 | 说明 |
|------|--------|------|
| `module` | EQ, LIKE | 模块名称 |
| `operation` | EQ, LIKE | 操作类型 |
| `username` | EQ, LIKE | 用户名 |
| `status` | EQ | 操作状态（0 失败 1 成功） |
| `createTime` | GT, LT, GE, LE, BETWEEN | 创建时间 |

---

## 六、日志清理

### 6.1 定期清理过期日志

**方式 1：手动清理**

```java
@Autowired
private SysOperationLogMapper operationLogMapper;

// 清理 90 天前的日志
Integer deletedCount = operationLogMapper.deleteLogsBeforeDays(90);
log.info("清理操作日志完成，删除 {} 条", deletedCount);
```

**方式 2：定时任务（推荐）**

```java
@Component
public class LogCleanupTask {

    @Autowired
    private SysOperationLogMapper operationLogMapper;

    // 每天凌晨 2 点执行
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanOldLogs() {
        Integer deletedCount = operationLogMapper.deleteLogsBeforeDays(90);
        log.info("定时清理操作日志完成，删除 {} 条", deletedCount);
    }
}
```

---

## 七、注意事项

### 7.1 性能优化

1. **异步保存**：已使用 `@Async` 异步保存日志，不影响业务性能
2. **参数长度限制**：请求参数限制 2000 字符，避免存储过大
3. **错误信息限制**：错误信息限制 1000 字符
4. **定期清理**：建议定期清理 90 天前的日志

### 7.2 安全考虑

1. **敏感信息**：请求参数中可能包含密码等敏感信息，建议：
   - 修改密码接口：在序列化前过滤掉密码字段
   - 或在注解中标记 `recordParams = false`（需扩展注解）

2. **日志权限**：操作日志应只允许管理员查看

### 7.3 IP 获取

已支持多种代理方式获取真实 IP：

- `X-Forwarded-For`：Nginx 代理
- `X-Real-IP`：Nginx 真实 IP
- `Proxy-Client-IP`：Apache 代理
- `WL-Proxy-Client-IP`：WebLogic 代理
- `RemoteAddr`：直连 IP

---

## 八、后续优化建议

1. **敏感信息脱敏**：扩展注解支持标记敏感字段，自动脱敏
2. **日志导出**：支持导出操作日志为 Excel
3. **日志统计**：按模块、操作、用户统计操作次数
4. **实时监控**：使用 WebSocket 实时推送操作日志
5. **日志归档**：定期将日志归档到对象存储

---

**文档更新日期**：2026-01-28
**作者**：ez-admin
