# 权限功能测试指南

## 测试环境准备

### 1. 启动项目
```bash
mvn spring-boot:run
```

### 2. 访问 Swagger 文档
```
http://localhost:8080/doc.html
```

---

## 测试流程

### 阶段一：系统初始化

#### 1.1 检查初始化状态
```http
GET /install/status
```

**预期响应**：
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "initialized": false,
    "hasAdmin": false
  }
}
```

#### 1.2 初始化系统
```http
POST /install
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123456",
  "nickname": "超级管理员"
}
```

**预期响应**：
```json
{
  "code": 0,
  "message": "系统初始化成功",
  "data": {
    "username": "admin",
    "userId": 1
  }
}
```

---

### 阶段二：用户登录

#### 2.1 登录获取 Token
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123456"
}
```

**预期响应**：
```json
{
  "code": 0,
  "message": "操作成功",
  "data": {
    "token": "eyJ0eXAiOiJqh...（token 字符串）",
    "userId": 1
  }
}
```

**重要**：保存返回的 `token`，后续请求需要在 Header 中携带：
```
satoken: eyJ0eXAiOiJqh...
```

---

### 阶段三：创建测试数据

#### 3.1 创建角色
```http
POST /role
Header: satoken: {你的token}
Content-Type: application/json

{
  "roleName": "普通管理员",
  "roleLabel": "admin",
  "roleSort": 1,
  "dataScope": 1,
  "status": 1,
  "description": "普通管理员角色"
}
```

#### 3.2 创建菜单（如果需要）
```http
POST /menu
Header: satoken: {你的token}
Content-Type: application/json

{
  "menuName": "用户管理",
  "menuLabel": "user",
  "parentId": 0,
  "menuSort": 1,
  "menuType": 2,
  "menuPerm": "system:user:list",
  "routePath": "/system/user",
  "status": 1
}
```

#### 3.3 为角色分配菜单
```http
POST /role/assign/menus
Header: satoken: {你的token}
Content-Type: application/json

{
  "roleId": 2,
  "menuIds": [1, 2, 3]
}
```

---

### 阶段四：权限校验测试

#### 4.1 创建测试用户
```http
POST /user
Header: satoken: {你的token}
Content-Type: application/json

{
  "username": "testuser",
  "password": "test123456",
  "nickname": "测试用户",
  "deptId": 1,
  "roleIds": [2]
}
```

#### 4.2 测试用户登录
```http
POST /auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "test123456"
}
```

保存新用户的 `token`。

#### 4.3 测试无权限访问
```http
GET /role/list
Header: satoken: {testuser 的 token}
```

**预期结果**：应该返回 403 Forbidden（因为没有分配相应权限）

#### 4.4 为测试用户分配权限
```http
POST /role/assign/menus
Header: satoken: {admin 的 token}
Content-Type: application/json

{
  "roleId": 2,
  "menuIds": [所有菜单ID]
}
```

#### 4.5 重新测试有权限访问
```http
GET /role/list
Header: satoken: {testuser 的 token}
```

**预期结果**：应该返回角色列表

---

## 权限字符串规范

| 权限字符串 | 说明 |
|-----------|------|
| `system:user:list` | 查询用户列表 |
| `system:user:create` | 创建用户 |
| `system:user:update` | 更新用户 |
| `system:user:delete` | 删除用户 |
| `system:role:list` | 查询角色列表 |
| `system:role:create` | 创建角色 |
| `system:menu:list` | 查询菜单列表 |
| `system:menu:create` | 创建菜单 |

---

## 常见问题排查

### 问题 1：401 Unauthorized
**原因**：未登录或 token 过期
**解决**：重新登录获取新 token

### 问题 2：403 Forbidden
**原因**：没有相应权限
**解决**：为角色分配对应菜单

### 问题 3：权限不生效
**检查项**：
1. 菜单的 `menu_perm` 字段是否填写
2. 角色是否分配了该菜单
3. 用户是否分配了该角色
4. 角色和菜单的状态是否为启用（status=1）

---

## Postman 测试集合

可以在 Postman 中创建以下环境变量：
- `baseUrl`: http://localhost:8080
- `token`: 登录后获取的 token

然后在 Headers 中配置：
- Key: `satoken`
- Value: `{{token}}`
