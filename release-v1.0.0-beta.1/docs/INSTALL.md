# EZ-ADMIN 安装指南

## 版本信息

- **版本号**：v1.0.0-beta.1
- **发布日期**：2026-01-28
- **状态**：测试版

---

## 环境要求

### 必需环境

- **JDK**: 21+
- **PostgreSQL**: 12+
- **Redis**: 5.0+（用于 Sa-Token 会话管理）

### 推荐配置

- **CPU**: 2 核心以上
- **内存**: 4GB 以上
- **磁盘**: 10GB 以上

---

## 快速开始

### 1. 数据库初始化

#### 1.1 创建数据库

```sql
CREATE DATABASE ez-admin
    WITH ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
```

#### 1.2 执行初始化脚本

```bash
# 连接到 PostgreSQL
psql -U postgres -d ez-admin

# 执行数据库结构脚本
\i sql/ez-admin-schema-v2-postgres.sql

# 执行菜单初始化脚本
\i sql/menu-init-data-v2-postgres.sql
```

或使用 PostgreSQL 客户端工具（如 pgAdmin、Navicat）依次执行：
- `sql/ez-admin-schema-v2-postgres.sql`
- `sql/menu-init-data-v2-postgres.sql`

### 2. 配置文件

#### 2.1 修改数据库连接

编辑 `config/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ez-admin
    username: postgres        # 修改为你的数据库用户名
    password: postgres        # 修改为你的数据库密码
```

#### 2.2 配置 Redis

```yaml
spring:
  data:
    redis:
      host: localhost         # Redis 主机地址
      port: 6379             # Redis 端口
      password:              # Redis 密码（无密码留空）
      database: 0            # Redis 数据库索引
```

#### 2.3 配置 Sa-Token

```yaml
sa-token:
  jwt-secret-key: your-secret-key-change-this  # 生产环境必须修改
  timeout: 2592000         # token 有效期（秒），默认 30 天
  active-timeout: 1800     # token 临时有效期（秒），默认 30 分钟
```

### 3. 启动应用

#### 方式一：命令行启动

```bash
java -jar ez-admin-springboot4-1.0.0-beta.1.jar
```

#### 方式二：指定配置文件

```bash
java -jar ez-admin-springboot4-1.0.0-beta.1.jar --spring.profiles.active=dev
```

#### 方式三：后台运行（Linux）

```bash
nohup java -jar ez-admin-springboot4-1.0.0-beta.1.jar > app.log 2>&1 &
```

### 4. 系统初始化

应用启动后，访问初始化接口创建超级管理员：

```bash
curl -X POST http://localhost:8080/install \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "nickname": "超级管理员"
  }'
```

**响应示例：**

```json
{
  "code": 0,
  "message": "系统初始化成功",
  "data": {
    "userId": "2016404540480516098",
    "username": "admin",
    "nickname": "超级管理员",
    "initTime": "2026-01-28 14:54:40"
  }
}
```

### 5. 登录测试

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**响应示例：**

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "userId": "2016404540480516098",
    "username": "admin",
    "nickname": "超级管理员"
  }
}
```

### 6. 访问 API 文档

启动成功后，访问 Swagger 文档：

```
http://localhost:8080/swagger-ui/index.html
```

---

## 常见问题

### Q1: 启动时提示"连接数据库失败"

**检查项：**
1. PostgreSQL 服务是否启动
2. 数据库连接配置是否正确（用户名、密码、端口）
3. 数据库是否已创建
4. 防火墙是否开放 PostgreSQL 端口（默认 5432）

### Q2: Redis 连接失败

**检查项：**
1. Redis 服务是否启动
2. Redis 连接配置是否正确
3. Redis 是否设置了密码

### Q3: 初始化接口返回"系统已初始化"

系统只能初始化一次。如需重新初始化：
```sql
-- 清空用户数据
DELETE FROM sys_user;
DELETE FROM sys_role;
DELETE FROM sys_user_role_relation;
```

### Q4: Token 过期怎么办？

默认 token 有效期为 30 天。过期后需要重新登录，或修改 `sa-token.timeout` 配置。

---

## 生产环境部署

### 安全建议

1. **修改默认密码**：超级管理员默认密码必须修改
2. **修改 JWT 密钥**：`sa-token.jwt-secret-key` 必须修改为复杂密钥
3. **关闭 Swagger**：生产环境关闭 API 文档
4. **配置 HTTPS**：使用反向代理（Nginx）配置 SSL 证书
5. **数据库权限**：创建专用数据库用户，不要使用 postgres 超级用户

### JVM 参数优化

```bash
java -Xms1g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar ez-admin-springboot4-1.0.0-beta.1.jar
```

---

## 技术支持

- **问题反馈**：[GitHub Issues](https://github.com/your-repo/ez-admin-springboot4/issues)
- **文档中心**：[项目文档](https://github.com/your-repo/ez-admin-springboot4/wiki)

---

## 许可证

本项目采用 [MIT License](LICENSE) 开源协议。
